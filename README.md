# RichEditText

A library for editing rich text in Android.

**Usage**

The class `RichTexter` enables us to add rich text support to any `TextView`. Pass the `TextView` instance to this class and the methods `getHtml()` and `setHtml()` allows us to get and set html source to the text view.

The class `RichEditTexter`, a subclass of `RichTexter`, enables rich text support to any `EditText`. Pass the `EditText` instance to this class and use the methods `apply...()` or `remove...()` to apply or remove markups. This class also contains a method called `onMarkupMenuClicked()` which we can call when a markup menu (like Bold, Italic) is clicked. This method takes care of whether to apply or remove or toggle the markup. This will be useful when we implement our own menu. 

**Extension**

This framework can be extended to support new markups (which are achievable using Android's spans) with minimal effort. To do so

  1. Create a class extending `Markup` (or one of its subclasses) and override the abstract methods.
  2. Add a markup menu for the newly created markup.
  3. On its click call `RichEditText.onMarkupMenuClicked()` by passing necessary params and the framework will take care of rest.
  
If you want the new markup to participate in html format conversion then do the following.

  4. While calling `RichTextView.setHtml()` pass a factory which returns the class of new markup for the new html tag. (You can use `HtmlUtil.defaultMarkupFactory` which returns the default markup classes for defaultly supported html tags.)
  5. Implement an `MarkupConverter.UnknownMarkupHandler` and pass it while calling `RichTextView.getHtml()`.
