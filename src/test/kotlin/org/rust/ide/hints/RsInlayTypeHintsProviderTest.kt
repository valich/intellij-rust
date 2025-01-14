/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.hints

import com.intellij.codeInsight.hints.presentation.PresentationRenderer
import com.intellij.openapi.vfs.VirtualFileFilter
import org.intellij.lang.annotations.Language
import org.rust.ProjectDescriptor
import org.rust.RsTestBase
import org.rust.WithStdlibRustProjectDescriptor
import org.rust.fileTreeFromText
import org.rust.lang.core.psi.RsMethodCall

class RsInlayTypeHintsProviderTest : RsTestBase() {
    fun `test simple`() = checkByText("""
        fn main() {
            let s/*hint text="[:  i32]"*/ = 42;
        }
    """)

    fun `test let stmt without expression`() = checkByText("""
        struct S;
        fn main() {
            let s/*hint text="[:  S]"*/;
            s = S;
        }
    """)

    fun `test no redundant hints`() = checkByText("""
        fn main() {
            let _ = 1;
            let _a = 1;
            let a = UnknownType;
        }
    """)

    fun `test smart hint don't show redundant hints`() = checkByText("""
        struct S;
        struct TupleStruct(f32);
        struct BracedStruct { f: f32 }
        enum E {
            C, B { f: f32 }, T(f32)
        }

        fn main() {
            let no_hint = S;
            let no_hint = TupleStruct(1.0);
            let no_hint = BracedStruct { f: 1.0 };
            let no_hint = E::C;
            let no_hint = E::B { f: 1.0 };
            let no_hint = E::T(1.0);
        }
    """)

    fun `test let decl tuple`() = checkByText("""
        struct S;
        fn main() {
            let (s/*hint text="[:  S]"*/, c/*hint text="[:  S]"*/) = (S, S);
        }
    """)

    fun `test pat field`() = checkByText("""
        struct S;
        struct TupleStruct(S);
        struct BracedStruct { a: S, b: S }
        fn main() {
            let TupleStruct(x/*hint text="[:  S]"*/) = TupleStruct(S);
            let BracedStruct { a: a/*hint text="[:  S]"*/, b/*hint text="[:  S]"*/ } = BracedStruct { a: S, b: S };
        }
    """)


    fun `test smart should not annotate tuples`() = checkByText("""
        enum Option<T> {
            Some(T),
            None
        }
        fn main() {
            let s = Option::Some(10);
        }
    """)

    private val fnTypes = """
        #[lang = "fn_once"]
        trait FnOnce<Args> { type Output; }

        #[lang = "fn_mut"]
        trait FnMut<Args>: FnOnce<Args> { }

        #[lang = "fn"]
        trait Fn<Args>: FnMut<Args> { }
    """

    fun `test lambda type hint`() = checkByText("""
        $fnTypes
        struct S;
        fn with_s<F: Fn(S)>(f: F) {}
        fn main() {
            with_s(|s/*hint text="[:  S]"*/| s.bar())
        }
    """)

    fun `test lambda type not shown if redundant`() = checkByText("""
        $fnTypes
        struct S;
        fn with_s<F: Fn(S)>(f: F) {}
        fn main() {
            with_s(|s: S| s.bar())
            with_s(|_| ())
        }
    """)

    fun `test lambda type should show after an defined type correct`() = checkByText("""
        $fnTypes
        struct S;
        fn foo<T: Fn(S, S, (S, S)) -> ()>(action: T) {}
        fn main() {
            foo(|x/*hint text="[:  S]"*/, y: S, z/*hint text="[:  [( [S ,  S] )]]"*/| {});
        }
    """)

    fun `test don't render horrendous types in their full glory`() = checkByText("""
        struct S<T, U>;

        impl<T, U> S<T, U> {
            fn wrap<F>(self, f: F) -> S<F, Self> {
                unimplemented!()
            }
        }

        fn main() {
            let s: S<(), ()> = unimplemented!();
            let foo/*hint text="[:  [S [< [[[fn( … )] [ →  … ]] ,  [S [< … >]]] >]]]"*/ = s
                .wrap(|x: i32| x)
                .wrap(|x: i32| x)
                .wrap(|x: i32| x)
                .wrap(|x: i32| x);
        }
    """)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test inlay hint for loops`() = checkByText("""
        struct S;
        struct I;
        impl Iterator for I {
            type Item = S;
            fn next(&mut self) -> Option<S> { None }
        }

        fn main() {
            for s/*hint text="[:  S]"*/ in I { }
        }
    """)

    fun `test don't touch ast`() {
        fileTreeFromText("""
        //- main.rs
            mod foo;
            use foo::Foo;

            fn main() {
                Foo.bar(92)
            }     //^
        //- foo.rs
            struct Foo;
            impl Foo { fn bar(&self, x: i32) {} }
        """).createAndOpenFileWithCaretMarker()

        val handler = RsInlayParameterHintsProvider()
        val target = findElementInEditor<RsMethodCall>("^")
        checkAstNotLoaded(VirtualFileFilter.ALL)
        val inlays = handler.getParameterHints(target)
        check(inlays.size == 1)
    }

    fun `test hints in if let expr`() = checkByText("""
        enum Option<T> {
            Some(T), None
        }
        fn main() {
            let result = Option::Some((1, 2));
            if let Option::Some((x/*hint text="[:  i32]"*/, y/*hint text="[:  i32]"*/)) = result {}
        }
    """)

    fun `test hints in if let expr with multiple patterns`() = checkByText("""
        enum V<T> {
            V1(T), V2(T)
        }
        fn main() {
            let result = V::V1((1, 2));
            if let V::V1(x/*hint text="[:  [( [i32 ,  i32] )]]"*/) | V::V2(x/*hint text="[:  [( [i32 ,  i32] )]]"*/) = result {}
        }
    """)

    fun `test hints in while let expr`() = checkByText("""
        enum Option<T> {
            Some(T), None
        }
        fn main() {
            let result = Option::Some((1, 2));
            while let Option::Some((x/*hint text="[:  i32]"*/, y/*hint text="[:  i32]"*/)) = result {}
        }
    """)

    fun `test hints in while let expr with multiple patterns`() = checkByText("""
        enum V<T> {
            V1(T), V2(T)
        }
        fn main() {
            let result = V::V1((1, 2));
            while let V::V1(x/*hint text="[:  [( [i32 ,  i32] )]]"/>) | V::V2(x<hint text="[:  [( [i32 ,  i32] )]]"*/) = result {}
        }
    """)

    fun `test hints in match expr`() = checkByText("""
        enum Option<T> {
            Some(T), None
        }
        fn main() {
            let result = Option::Some((1, 2));
            match result {
                Option::Some((x/*hint text="[:  i32]"*/, y/*hint text="[:  i32]"*/)) => (),
                _ => ()
            }
        }
    """)

    fun `test show hints only for new local variables and ignore enum variants`() = checkByText("""
        enum Option<T> {
            Some(T), None
        }

        use Option::{Some, None};

        fn main() {
            let result = Some(1);
            match result {
                None => (),
                Name/*hint text="[:  [Option [< i32 >]]]"*/ => ()
            }
        }
    """)

    fun `test show hints for inner pat bindings`() = checkByText("""
        enum Option<T> {
            Some(T), None
        }

        use Option::{Some, None};

        fn main() {
            match Option::Some((1, 2)) {
                Some((x/*hint text="[:  i32]"*/, 5)) => (),
                y => ()
            }
        }
    """)

    @ProjectDescriptor(WithStdlibRustProjectDescriptor::class)
    fun `test show hints for slice`() = checkByText("""
        fn main() {
            let xs/*hint text="[:  [& [[ i32 ]]]]"*/ = &vec![1,2,3][..];
        }
    """)

    fun `test dyn trait with assoc type`() = checkByText("""
        trait Trait { type Item; }
        fn foo(x: &Trait<Item=u8>) {
            let a/*hint text="[:  [& [dyn  [Trait [< [Item = u8] >]] ]]]"*/ = x;
        }
    """)

    fun `test impl trait with assoc type`() = checkByText("""
        trait Trait { type Item; }
        fn bar() -> impl Trait<Item=u8> { unimplemented!() }
        fn foo() {
            let a/*hint text="[:  [impl  [Trait [< [Item = u8] >]] ]]"*/ = bar();
        }
    """)

    fun `test projection type`() = checkByText("""
        trait Trait { type Item; }
        fn foo<T: Trait>(x: T::Item) {
            let a/*hint text="[:  [[< [T  as  Trait] >] :: Item]]"*/ = x;
        }
    """)

    private fun checkByText(@Language("Rust") code: String) {
        InlineFile(code.replace(HINT_COMMENT_PATTERN, "<$1/>"))

        myFixture.testInlays(
            { (it.renderer as PresentationRenderer).presentation.toString() },
            { it.renderer is PresentationRenderer }
        )
    }

    companion object {
        private val HINT_COMMENT_PATTERN = Regex("""/\*(hint.*?)\*/""")
    }
}
