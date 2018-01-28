# RichEditText

A library for editing rich text in Android.

The class `RichTexter` enables us to add rich text support to any `TextView`. Pass the `TextView` instance to this class and the 
methods `getHtml()` and `setHtml()` allows us to get and set html source to the text view.

The class `RichEditTexter`, a subclass of `RichTexter`, enables rich text support to any `EditText`. Pass the `EditText` instance to this class and use the 
methods `apply...()` or `remove...()` to apply or remove markups. This class also contains a method called `onMarkupMenuClicked()`
which we can call when a markup menu (like Bold, Italic) is clicked. This method takes care of whether to apply or remove or
toggle the markup. This will be useful when we implement our own menu. 
