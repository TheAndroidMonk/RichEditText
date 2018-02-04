# RichEditText

A library for displaying/editing rich text in Android.

**This framework is under development and may contain bugs and limitations.**

## Usage

This library provides ready to use widgets, `RichTextView` and `RichEditText`, for diplaying and editing rich text. It also has classes to add rich text feature to any `TextView` or `EditText`.

The class `RichTexter` enables us to add rich text support to any `TextView`. Pass the `TextView` instance to this class and the methods `getHtml()` and `setHtml()` allows us to get and set html source to the text view.

The class `RichEditTexter`, a subclass of `RichTexter`, enables rich text support to any `EditText`. Pass the `EditText` instance to this class and use the methods `apply...()` or `remove...()` to apply or remove markups. This class also contains a method called `onMarkupMenuClicked()` which we can call when a markup menu (like Bold, Italic) is clicked. This method takes care of whether to apply or remove or toggle the markup. This will be useful when we implement our own menu.

Currently supported markups are

  1. Bold
  2. Italic
  3. Underline
  4. Strikethrough
  5. Link
  6. Font (typeface, size, color, background color)
  7. Ordered List
  8. Unordered List
  9. Paragraph (alignment, top spacing, bottom spacing)
  10. Superscript
  11. Subscript
  
**Note**

_There is no exact analogy between the markups of this framework and html tags. For example Paragraph markup supports top and bottom spacing but html's `<p>` don't. Hence while converting to and from html some styling may be discarded or lost. Sometimes the same markups could be used for different styles. For exampe both the foreground and background color styles are achieved using Font markup._

**Current Limitations**

  * No support for full justification in Paragraph markup.
  * Ordered and Unordered lists do _not_ automatically add list items when a new-line is entered at the end of the list.
  
## Extension

This framework can be extended to support new markups (which are achievable using Android's spans) with minimal effort. To do so

  1. Create a class extending `Markup` (or one of its subclasses) and override the abstract methods.
  2. Add a markup menu for the newly created markup.
  3. On its click call `RichEditText.onMarkupMenuClicked()` by passing necessary params and the framework will take care of rest.
  
If you want the new markup to participate in html format conversion then do the following.

  4. While calling `RichTextView.setHtml()` pass a factory which returns the class of new markup for the new html tag. (You can use `HtmlUtil.defaultMarkupFactory` which returns the default markup classes for defaultly supported html tags.)
  5. Implement an `MarkupConverter.UnknownMarkupHandler` and pass it while calling `RichTextView.getHtml()`.
