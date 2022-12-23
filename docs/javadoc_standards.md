# Javadoc coding standards

Javadoc is a key part of coding in Java. This document describes what makes good Javadoc style in iTestCloud. They are the guidelines and standards to follow when writing Javadoc. It is more about the formatting of Javadoc, than the content of Javadoc.

### Treat Javadoc as source code

Javadoc is not only to generate the Javadoc HTML page, but also to help understand the source code written by others. Making Javadoc readable as source code is critical, and these standards are guided by this principal.

### Use the standard style for the Javadoc comment

Javadoc only requires a `/**` at the start and a `*/` at the end. In addition to this, use a single star on each additional line:

```Java
/**
 * Standard comment.
 */
public foo()
```

Do **<ins>NOT</ins>** use more than two asterisks at the start, for example, `/***`, or more than one asterisk at the end of the Javadoc, like `**/`.

### Add Javadoc for `public` and `protected` methods

All `public` and `protected` methods shall be fully defined with Javadoc. The `private` methods do not have to be, but may benefit from it, too. If a method is overridden in a subclass, Javadoc should only be present if it says something different to the original definition of the method. For example, this is a public method:

```Java
/**
 * Gets this page content.
 *
 * @return this page instance.
 */
public final Page getPage() {
	...
	return this;
}
```

This is a protected method:

```Java
/**
 * Returns a list of frames available in the web page.
 * <p>
 * Any frame within another frame (child frames) will not be included by this list.
 * </p>
 *
 * @return a list of frames available in the web page as {@link List}
 */
protected List<BrowserElement> getFrames() {
	return waitForElements(By.xpath("//iframe"), false /*fail*/, tinyTimeout());
}
```

### Define the descriptive and informative first sentence

The first sentence of each doc comment should be a summary sentence, containing a concise but complete description of the API item, including each `public` and `protected` method, class, or interface. The Javadoc tool copies this first sentence to the appropriate member, class/interface or package summary. This makes it important to write crisp and informative initial sentences that can stand on their own.

This sentence ends at the first period that is followed by a blank, tab, or line terminator, or at the first tag (as defined below). For example, this first sentence ends at "Prof.":

```Java
/**
 * This is a simulation of Prof. Knuth's MIX computer.
 */
```

However, you can work around this by typing an HTML meta-character such as "&" immediately after the period, such as:

```Java
/**
 * This is a simulation of Prof.&nbsp;Knuth's MIX computer.
 */
```

In particular, write summary sentences that distinguish overloaded methods from each other. For example:

```Java
/** 
 * Class constructor.
 */
foo() {
...

/**
 * Class constructor specifying number of objects to create.
 */
foo(final int n) {
...
```

### Use `<code>` style for keywords and names
Keywords and names are offset by `<code>...</code>` when mentioned in a description. This includes:

- Java keywords
- package names
- class names
- method names
- interface names
- field names
- argument names
- code examples

For example, the following method Javadoc contains some of the above keywords:

```Java
/**
 * Purchases the service.
 *
 * @param name The name of the service. <code>null</code> should be specified as the value of this parameter
 * if the default name shall be used.
 * @param region The region of the service as {@link Region}. <code>null</code> should be specified as the
 * value of this parameter if the default region should be used.
 * @param plan The plan of the service. <code>null</code> should be specified as the value of this parameter
 * if the default space should be used.
 * @param openedPageClass The class representing the service page that extends from {@link Page}.
 *
 * <p>
 * For example:
 * <code>
 * new ApsPortalConfirmPurchaseDialog(this).purchaseService(name, region, plan, openedPageClass);
 * </code>
 * </p>
 *
 * @return the opened service page that extends from <code>com.ibm.itest.cloud.common.pages.Page</code>.
 */
public <P extends Page> P purchaseService(final String name, final Region region, final String plan,
	final Class<P> openedPageClass) {
	...
}
```

### Define `null`-handling for parameters and return types
Whether a method accepts `null` on input, or may return `null` is critical information for iTestCloud. All non-primitive methods should define their `null`-tolerance in the @param or @return, wherever and whenever appropriate. For example, refer to the above sample.

### OK to use phrases instead of complete sentences, in the interests of brevity
This holds especially in the initial summary and in `@param` tag descriptions.

### Use 3rd person (descriptive) not 2nd person (prescriptive)
The description is in 3rd person declarative rather than 2nd person imperative.

- Gets the label. (preferred)
- Get the label. (avoid)

For example, the following method's description is in 3rd person:

```Java
/**
 * Gets the label associated with the Scoring Data Source Type.
 *
 * @return the label associated with Scoring Data Source Type.
 */
public Pattern getLabel() {
	return Pattern.compile(this.label);
}
```

### Use a verb phrase for method descriptions
A method implements an operation, so it usually starts with a verb phrase:

Gets the label of this button. (preferred)

This method gets the label of this button.

### Aim for short single line sentences

Wherever possible, make Javadoc sentences fit on a single line. Allow flexibility in the line length, preferably between 80 and 120 characters.

### Use "this" instead of "the" when referring to an object created from the current class
For example, the description of the `getText` method should read as follows:

Gets the text in this element. (preferred)

Gets the text in the element. (avoid)

For example, the following method's description explicitly uses "this" instead of "the":

```Java
/**
 * Gets this page content.
 *
 * @return this page instance.
 */
public final Page getPage() {
	...
	return this;
}
```

### Avoid Latin
Use "also known as" instead of "aka", use "that is" or "to be specific" instead of "i.e.", use "for example" instead of "e.g.", and use "in other words" or "namely" instead of "viz."

### Use one blank line before @param
There should be one blank line between the Javadoc text and the first `@param` or `@return`. This improves readability in source code.

### Treat @throws as an if clause
The `@throws` feature should normally be followed by "if" and the rest of the phrase describing the condition. For example, `@throws IllegalArgumentException if the file could not be found`. This improves readability in source code and when generated.

For example, the following method may throw ScenarioFailedError:

```Java
/**
* Checks if the specified file exists; outputs to debug log if so.
*
* @param file The specified {@link File} to check.
* @throws ScenarioFailedError if the file does not exist.
*/
public static void check(final File file) throws ScenarioFailedError {
	...
}
```

### Avoid `@author`

The tag `@author` should be avoided in iTestCloud project.