/*
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */

package org.rust.ide.colors

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors as Default

/**
 * See [RsColorSettingsPage] and [org.rust.ide.highlight.RsHighlighter]
 */
enum class RsColor(humanName: String, val default: TextAttributesKey? = null) {
    IDENTIFIER("Variables//Default", Default.IDENTIFIER),
    MUT_BINDING("Variables//Mutable binding", Default.IDENTIFIER),
    FIELD("Variables//Field", Default.INSTANCE_FIELD),
    CONSTANT("Variables//Constant", Default.CONSTANT),

    FUNCTION("Functions//Function declaration", Default.FUNCTION_DECLARATION),
    METHOD("Functions//Method declaration", Default.INSTANCE_METHOD),
    ASSOC_FUNCTION("Functions//Associated function declaration", Default.STATIC_METHOD),
    FUNCTION_CALL("Functions//Function call", Default.FUNCTION_CALL),
    METHOD_CALL("Functions//Method call", Default.FUNCTION_CALL),
    ASSOC_FUNCTION_CALL("Functions//Associated function call", Default.STATIC_METHOD),
    MACRO("Functions//Macro", Default.IDENTIFIER),

    PARAMETER("Parameters//Parameter", Default.PARAMETER),
    MUT_PARAMETER("Parameters//Mutable parameter", Default.PARAMETER),
    SELF_PARAMETER("Parameters//Self parameter", Default.KEYWORD),
    LIFETIME("Parameters//Lifetime", Default.IDENTIFIER),
    TYPE_PARAMETER("Parameters//Type parameter", Default.IDENTIFIER),
    CONST_PARAMETER("Parameters//Const parameter", Default.CONSTANT),

    PRIMITIVE_TYPE("Types//Primitive", Default.KEYWORD),
    STRUCT("Types//Struct", Default.CLASS_NAME),
    UNION("Types//Union", Default.CLASS_NAME),
    TRAIT("Types//Trait", Default.INTERFACE_NAME),
    ENUM("Types//Enum", Default.CLASS_NAME),
    ENUM_VARIANT("Types//Enum variant", Default.STATIC_FIELD),
    TYPE_ALIAS("Types//Type alias", Default.CLASS_NAME),
    CRATE("Types//Crate", Default.IDENTIFIER),
    MODULE("Types//Module", Default.IDENTIFIER),

    KEYWORD("Keywords//Keyword", Default.KEYWORD),
    KEYWORD_UNSAFE("Keywords//Unsafe", Default.KEYWORD),

    CHAR("Literals//Char", Default.STRING),
    NUMBER("Literals//Number", Default.NUMBER),
    STRING("Literals//Strings//String", Default.STRING),
    VALID_STRING_ESCAPE("Literals//Strings//Escape sequence//Valid", Default.VALID_STRING_ESCAPE),
    INVALID_STRING_ESCAPE("Literals//Strings//Escape sequence//Invalid", Default.INVALID_STRING_ESCAPE),

    BLOCK_COMMENT("Comments//Block comment", Default.BLOCK_COMMENT),
    EOL_COMMENT("Comments//Line comment", Default.LINE_COMMENT),

    DOC_COMMENT("Rustdoc//Comment", Default.DOC_COMMENT),
    DOC_HEADING("Rustdoc//Heading", Default.DOC_COMMENT_TAG),
    DOC_LINK("Rustdoc//Link", Default.DOC_COMMENT_TAG_VALUE),
    DOC_CODE("Rustdoc//Code", Default.DOC_COMMENT_MARKUP),

    BRACES("Braces and Operators//Braces", Default.BRACES),
    BRACKETS("Braces and Operators//Brackets", Default.BRACKETS),
    OPERATORS("Braces and Operators//Operation sign", Default.OPERATION_SIGN),
    Q_OPERATOR("Braces and Operators//? operator", Default.KEYWORD),
    SEMICOLON("Braces and Operators//Semicolon", Default.SEMICOLON),
    DOT("Braces and Operators//Dot", Default.DOT),
    COMMA("Braces and Operators//Comma", Default.COMMA),
    PARENTHESES("Braces and Operators//Parentheses", Default.PARENTHESES),

    ATTRIBUTE("Attribute", Default.METADATA),
    UNSAFE_CODE("Unsafe code"),
    CFG_DISABLED_CODE("Conditionally disabled code"),
    ;

    val textAttributesKey = TextAttributesKey.createTextAttributesKey("org.rust.$name", default)
    val attributesDescriptor = AttributesDescriptor(humanName, textAttributesKey)
    val testSeverity: HighlightSeverity = HighlightSeverity(name, HighlightSeverity.INFORMATION.myVal)
}

