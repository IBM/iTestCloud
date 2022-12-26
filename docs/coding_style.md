# Java Style Guide

The intention of this guide is to provide a set of conventions that encourage good code.
While some suggestions are more strict than others, you should always practice good judgment.

In general, much of our style and conventions mirror the
[Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)
and [Google's Java Style Guide](https://google.github.io/styleguide/javaguide.html).

## Table of contents

- [Coding style](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#coding-style)
  - [Formatting](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#formatting)
  - [Field, class, and method declarations](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#field-class-and-method-declarations)
  - [Variable naming](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#variable-naming)
  - [Space pad operators and equals](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#space-pad-operators-and-equals)
  - [Be explicit about operator precedence](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#be-explicit-about-operator-precedence)
  - [Empty blocks: may be concise](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#empty-blocks:-may-be-concise)
  - [Imports](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#imports)
- [Best practices](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#best-practices)
  - [Defensive programming](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#defensive-programming)
  - [De-nest when it exceeds 3-deep](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#De-nest-when-it-exceeds-3-deep)
  - [Clean code](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#clean-code)
  - [Use newer/better libraries](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#use-newerbetter-libraries)
  - [equals() and hashCode()](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#equals-and-hashcode)
  - [TODOs](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#todos)
  - [Obey the Law of Demeter](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#obey-the-law-of-demeter-lod)
  - [Don't Repeat Yourself](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#dont-repeat-yourself-dry)
  - [Avoid unnecessary code](https://github.com/IBM/iTestCloud/tree/master/docs/coding_style.md#avoid-unnecessary-code)

## Coding style

### Formatting

#### Use line breaks wisely
There are generally two reasons to insert a line break:

1. Your statement is too long to follow the meaning of it.

2. You want to logically separate a thought.<br />
Writing code is like telling a story.  Written language constructs like chapters, paragraphs,
and punctuation (e.g. semicolons, commas, periods, hyphens) convey thought hierarchy and
separation.  We have similar constructs in programming languages; you should use them to your
advantage to effectively tell the story to those reading the code.

#### Indent style
We use the "one true brace style" ([1TBS](http://en.wikipedia.org/wiki/Indent_style#Variant:_1TBS)).
Indent size is 4 columns.

```Java
// Like this.
if (x < 0) {
    negative(x);
} else {
    nonnegative(x);
}

// Not like this.
if (x < 0)
    negative(x);

// Allowed.
if (x < 0) negative(x);
```

Continuation indent is 4 columns.  Nested continuations may add 4 columns at each level.

```Java
// Bad.
//   - Line breaks are arbitrary.
//   - Scanning the code makes it difficult to piece the message together.
throw new IllegalStateException("Failed to process request" + request.getId()
    + " for user " + user.getId() + " query: '" + query.getText()
    + "'");

// Good.
//   - Each component of the message is separate and self-contained.
//   - Adding or removing a component of the message requires minimal reformatting.
throw new IllegalStateException("Failed to process"
    + " request " + request.getId()
    + " for user " + user.getId()
    + " query: '" + query.getText() + "'");
```

Don't break up a statement unnecessarily.

```Java
// Bad.
final String value =
    otherValue;

// Good.
final String value = otherValue;
```

Method declaration continuations.

```Java
// Sub-optimal since line breaks are arbitrary and only filling lines.
String downloadAnInternet(Internet internet, Tubes tubes,
    Blogosphere blogs, Amount<Long, Data> bandwidth) {
    tubes.download(internet);
    ...
}

// Acceptable.
String downloadAnInternet(Internet internet, Tubes tubes, Blogosphere blogs,
    Amount<Long, Data> bandwidth) {
    tubes.download(internet);
    ...
}

// Nicer, as the extra newline gives visual separation to the method body.
String downloadAnInternet(Internet internet, Tubes tubes, Blogosphere blogs,
    Amount<Long, Data> bandwidth) {

    tubes.download(internet);
    ...
}

// Also acceptable, but may be awkward depending on the column depth of the opening parenthesis.
public String downloadAnInternet(Internet internet,
                                 Tubes tubes,
                                 Blogosphere blogs,
                                 Amount<Long, Data> bandwidth) {
    tubes.download(internet);
    ...
}
```

The static fields, initializers, and methods and fields need to have indentation. Class fields,
initializers, constructors, and methods have *no* indentation, starting at the same column of
the class.

```Java
class Foo {
    static final int id = 0;
    
    static {
        HISTORY.set(new ArrayList<Event>());
    }
    
    public static void clearHistory() {
        getHistory().clear();
    }

private final int id;

public Foo() {
    id = 10;
}

public int getId() {
    return this.id;
}
```

##### Chained method calls

```Java
// Bad.
//   - Line breaks are based on line length, not logic.
Iterable<Module> modules = ImmutableList.<Module>builder().add(new LifecycleModule())
    .add(new AppLauncherModule()).addAll(application.getModules()).build();

// Good.
//   - Method calls are isolated to a line.
//   - The proper location for a new method call is unambiguous.
Iterable<Module> modules = ImmutableList.<Module>builder()
    .add(new LifecycleModule())
    .add(new AppLauncherModule())
    .addAll(application.getModules())
    .build();
```

#### No tabs (TO BE DISCUSSED)
An oldie, but goodie.  It is found tab characters cause more harm than good.

#### CamelCase for types, camelCase for variables, UPPER_SNAKE for constants
Sometimes there is more than one reasonable way to convert an English phrase into camel case,
such as when acronyms or unusual constructs like "IPv6" or "iOS" are present. To improve
predictability, the steps below specify how to come up with the correct camel case.

Beginning with the prose form of the name:

1. Convert the phrase to plain ASCII and remove any apostrophes. For example, "Müller's algorithm"
becomes "Muellers algorithm".
2. Divide this result into words, splitting on spaces and any remaining punctuation (typically
hyphens).<br >
*Recommended*: if any word already has a conventional camel-case appearance in common
usage, split this into its constituent parts (e.g., "AdWords" becomes "ad words"). Note that a word
such as "iOS" is not really in camel case per se; it defies any convention, so this recommendation
does not apply.
3. Now lowercase everything (including acronyms), then uppercase only the first character of:
  o ... each word, to yield upper camel case, or
  o ... each word except the first, to yield lower camel case
4. Finally, join all the words into a single identifier.

Note that the casing of the original words is almost entirely disregarded. Examples:

| Prose form | Correct | Incorrect |
| ---------- | ------- | --------- |
| "XML HTTP request" | `XmlHttpRequest` | `XMLHTTPRequest` |
| "new customer ID" | `newCustomerId` | `newCustomerID` |
| "inner stopwatch" | `innerStopwatch` | `innerStopWatch` |
| "supports IPv6 on iOS?" | `supportsIpv6OnIos` | `supportsIPv6OnIOS` |
| "YouTube importer" | `YouTubeImporter` or `YoutubeImporter` * | |

\* Acceptable, but not recommended.

__Note__: Some words are ambiguously hyphenated in the English language: for example "nonempty" and
"non-empty" are both correct, so the method names checkNonempty and checkNonEmpty are likewise both
correct.

#### No trailing whitespace
Trailing whitespace characters, while logically benign, add nothing to the program.
However, they do serve to frustrate developers when using keyboard shortcuts to navigate code.

### Field, class, and method declarations

##### Modifier order

We follow the [Java Language Specification](http://docs.oracle.com/javase/specs/) for modifier
ordering (sections
[8.1.1](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.1.1),
[8.3.1](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1) and
[8.4.3](http://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.4.3)).

1. public
2. protected
3. private
4. abstract
5. default
6. static
7. sealed
8. non-sealed
9. final
10. transient
11. volatile
12. synchronized
13. native
14. strictfp

```Java
// Bad.
final volatile private String value;

// Good.
private final volatile String value;
```

### Variable naming

#### Extremely short variable names should be reserved for instances like loop indices.

```Java
// Bad.
//   - Field names give little insight into what fields are used for.
class User {
private final int a;
private final String m;

...
}

// Good.
class User {
private final int ageInYears;
private final String maidenName;

...
}
```

#### Include units in variable names (NOT SURE, TO BE DISCUSSED)

```Java
// Bad.
long pollInterval;
int fileSize;

// Good.
long pollIntervalMs;
int fileSizeGb.

// Better.
//   - Unit is built in to the type.
//   - The field is easily adaptable between units, readability is high.
Amount<Long, Time> pollInterval;
Amount<Integer, Data> fileSize;
```

#### Don't embed metadata in variable names
A variable name should describe the variable's purpose.  Adding extra information like scope and
type is generally a sign of a bad variable name.

Avoid embedding the field type in the field name.

```Java
// Bad.
Map<Integer, User> idToUserMap;
String valueString;

// Good.
Map<Integer, User> usersById;
String value;
```

Also avoid embedding scope information in a variable.  Hierarchy-based naming suggests that a class
is too complex and should be broken apart.

```Java
// Bad.
String _value;
String mValue;

// Good.
String value;
```

### Space pad operators and equals.

```Java
// Bad.
//   - This offers poor visual separation of operations.
int foo=a+b+1;

// Good.
int foo = a + b + 1;
```

### Be explicit about operator precedence
Don't make your reader open the
[spec](http://docs.oracle.com/javase/tutorial/java/nutsandbolts/operators.html) to confirm,
if you expect a specific operation ordering, make it obvious with parenthesis, whenever necessary.

```Java
// Bad.
return a << 8 * n + 1 | 0xFF;

// Good.
return (a << (8 * n) + 1) | 0xFF;
```

It's even good to be *really* obvious.

```Java
if ((values != null) && (10 > values.size())) {
    ...
}
```

### Empty blocks: may be concise
An empty block or block-like construct may be in K & R style. Alternatively, it may be closed
immediately after it is opened, with no characters or line break in between (`{}`), *unless* it is
part of a multi-block statement (one that directly contains multiple blocks: `if/else` or
`try/catch/finally`).

```Java
// This is acceptable
void doNothing() {}

// This is equally acceptable
void doNothingElse() {
}

// Bad: No concise empty blocks in a multi-block statement
try {
    doSomething();
} catch (Exception e) {}
```

### Imports

#### Import ordering
Imports are grouped by top-level package.  Static imports shall be put first, and are grouped in
the same way, in a section below traditional imports.

```Java
import static *

import com.*
import java.*
import javax.*
import net.*
import org.*
import scala.*
```

#### Group imports
Imports are ordered as follows:

- All static imports in a single block.
- All non-static imports in a single block.

If there are both static and non-static imports, a single blank line separates the two blocks.
There are no other blank lines between import statements.  Within each block the imported names
shall appear in ASCII sort order.

#### Be cautious to use wildcard imports
Wildcard imports shall be used cautiously and in an absolutely necessary situation, as they make
the source of an imported class less clear.  They also tend to hide a high class fan-out.<br />

```Java
// Bad.
//   - Where did Foo come from?
import com.twitter.baz.foo.*;
import com.twitter.*;

interface Bar extends Foo {
    ...
}

// Good.
import com.twitter.baz.foo.BazFoo;
import com.twitter.Foo;

interface Bar extends Foo {
    ...
}
```

## Best practices

### Defensive programming

#### Avoid assert
We avoid the assert statement since it can be
[disabled](http://docs.oracle.com/javase/7/docs/technotes/guides/language/assert.html#enable-disable)
at execution time, and prefer to enforce these types of invariants at all times.

*See also [preconditions](#preconditions)*

#### Preconditions
Preconditions checks are a good practice, since they serve as a well-defined barrier against bad
input from callers.  As a convention, object parameters to public constructors and methods should
always be checked against null, unless null is explicitly allowed.

*See also [be wary of null](#be-wary-of-null)*

```Java
// Bad.
//   - If the file or callback are null, the problem isn't noticed until much later.
class AsyncFileReader {
void readLater(File file, Closure<String> callback) {
    scheduledExecutor.schedule(new Runnable() {
        @Override
        public void run() {
            callback.execute(readSync(file));
        }
    }, 1L, TimeUnit.HOURS);
}
}

// Good.
class AsyncFileReader {
void readLater(File file, Closure<String> callback) {
    checkNotNull(file);
    checkArgument(file.exists() && file.canRead(), "File must exist and be readable.");
    checkNotNull(callback);

    scheduledExecutor.schedule(new Runnable() {
        @Override
        public void run() {
            callback.execute(readSync(file));
        }
    }, 1L, TimeUnit.HOURS);
}
}
```

#### Minimize visibility

In a class API, you should support access to any methods and fields that you make accessible.
Therefore, only expose what you intend the caller to use.  This can be imperative when
writing thread-safe code.

```Java
public class Parser {
// Bad.
//   - Callers can directly access and mutate, possibly breaking internal assumptions.
public Map<String, String> rawFields;

// Bad.
//   - This is probably intended to be an internal utility function.
public String readConfigLine() {
    ..
}
}

// Good.
//   - rawFields and the utility function are hidden
//   - The class is package-private, indicating that it should only be accessed indirectly.
class Parser {
private final Map<String, String> rawFields;

private String readConfigLine() {
    ..
}
}
```

#### Favor immutability

Mutable objects carry a burden - you need to make sure that those who are *able* to mutate it are
not violating expectations of other users of the object, and that it's even safe for them to modify.

```Java
// Bad.
//   - Anyone with a reference to User can modify the user's birthday.
//   - Calling getAttributes() gives mutable access to the underlying map.
public class User {
public Date birthday;
private final Map<String, String> attributes = Maps.newHashMap();

...

public Map<String, String> getAttributes() {
    return attributes;
}
}

// Good.
public class User {
private final Date birthday;
private final Map<String, String> attributes = Maps.newHashMap();

...

public Map<String, String> getAttributes() {
    return ImmutableMap.copyOf(attributes);
}

// If you realize the users don't need the full map, you can avoid the map copy
// by providing access to individual members.
@Nullable
public String getAttribute(String attributeName) {
    return attributes.get(attributeName);
}
}
```

#### Be wary of null
Use `@Nullable` where prudent, but favor Optional over `@Nullable`.  `Optional` provides better
semantics around absence of a value.

#### Clean up with finally

```Java
FileInputStream in = null;
try {
    ...
} catch (IOException e) {
    ...
} finally {
    Closeables.closeQuietly(in);
}
```

Even if there are no checked exceptions, there are still cases where you should use try/finally
to guarantee resource symmetry.

```Java
// Bad.
//   - Mutex is never unlocked.
mutex.lock();
throw new NullPointerException();
mutex.unlock();

// Good.
mutex.lock();
try {
    throw new NullPointerException();
} finally {
    mutex.unlock();
}

// Bad.
//   - Connection is not closed if sendMessage throws.
if (receivedBadMessage) {
    conn.sendMessage("Bad request.");
    conn.close();
}

// Good.
if (receivedBadMessage) {
    try {
        conn.sendMessage("Bad request.");
    } finally {
        conn.close();
    }
}
```

### De-nest when it exceeds 3-deep
Nesting code is when you add more inner blocks to a function.  We'll consider each open brace to be
adding one more depth to the function.  Deeply nesting code will dramatically increase the amount
of conditions your brain must simultaneously hold.

The nest code beyond 3-deep is discouraging.  In the most cases, the function has more than 3-deep
nest code suggests a need of code refactoring to de-nest it.

There are two methods to de-nest the code:
1. Extraction: pull out the part of the code into its own function.
2. Inversion: flip conditions and switch to an early return.

```Java
// Bad.
//   - 4-deep nest code. Not easy to apprehend the logic and conditions. 
int calculate(int bottom, int top) {                          // 1-deep
    if (top > bottom) {                                       // 2-deep
        int sum = 0;

        for (int number = bottom; number <= top; number++) {  // 3-deep
            if (number % 2 == 0) {                            // 4-deep
                sum += number;
            }
        }

        return sum;
    } else {
        return 0;
    }
}

// Good.
//   - Code just has 2-deep nest, flattened by extraction and inversion.
int filterNumber(int number) {
    if (number % 2 == 0) {
        return number;
    }
    
    return 0;
}

int calculate(int bottom, int top) {
    if (top < bottom) {
        return 0;
    }

    int sum = 0;

    for (int number = bottom; number <= top; number++) {
        sum += filterNumber(number);
    }

    return sum;
}
```

### Clean code

#### Use correct source file structure
A source file consists of, in order:

1. License or copyright information, if present
2. Package statement
3. Import statements
4. Exactly one top-level class

*Exactly one blank line* separates each section that is present.



#### Ensure the correct member order
All members in the class shall be sorted according to the following order:
- Static fields
- Static initializers
- Static methods
- Fields
- Initializers
- Constructors
- Methods

Check the Eclipse members sort order abide by the above rule. The setting is at Eclipse > Settings >
Java > Appearance > Members Sort Order.

After making any changes to the class, use Eclipse > Source > Sort Members to apply the sorting
rules.

#### Disambiguate
Favor readability - if there's an ambiguous and unambiguous route, always favor unambiguous.

```Java
// Bad.
//   - Depending on the font, it may be difficult to discern 1001 from 100l.
long count = 100l + n;

// Good.
long count = 100L + n;

// Bad.
//   - Hard to read for large numbers.
int million = 1000000;

// Good
int million = 1_000_000;
```

#### Remove dead code
Delete unused code (imports, fields, parameters, methods, classes).  They will only rot.

#### Use general types
When declaring fields and methods, it's better to use general types whenever possible.
This avoids implementation detail leak via your API, and allows you to change the types used
internally without affecting users or peripheral code.

```Java
// Bad.
//   - Implementations of Database must match the ArrayList return type.
//   - Changing return type to Set<User> or List<User> could break implementations and users.
interface Database {
    ArrayList<User> fetchUsers(String query);
}

// Good.
//   - Iterable defines the minimal functionality required of the return.
interface Database {
    Iterable<User> fetchUsers(String query);
}

// Good.
//   - List allowing ArrayList, HashList, etc. makes the return type more flexible.
interface Database {
    List<User> fetchUsers(String query);
}
```

#### Always use type parameters
Java 5 introduced support for
[generics](http://docs.oracle.com/javase/tutorial/java/generics/index.html). This added type
parameters to collection types, and allowed users to implement their own type-parameterized classes.
Backwards compatibility and
[type erasure](http://docs.oracle.com/javase/tutorial/java/generics/erasure.html) mean that
type parameters are optional, however depending on usage they do result in compiler warnings.

We conventionally include type parameters on every declaration where the type is parameterized.
Even if the type is unknown, it's preferable to include a wildcard or wide type.

#### Try to avoid typecasting
Typecasting is a sign of poor class design, and can often be avoided.  An obvious exception here is
overriding
[equals](http://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#equals(java.lang.Object)).

#### Use final fields
*See also [favor immutability](#favor-immutability)*

Final fields are useful because they declare that a field may not be reassigned.  When it comes to
checking for thread-safety, a final field is one less thing that needs to be checked.

#### Avoid mutable static state
Mutable static state is rarely necessary, and causes loads of problems when present.  A very simple
case that mutable static state complicates is unit testing.  Since unit tests runs are typically in
a single VM, static state will persist through all test cases.  In general, mutable static state is
a sign of poor class design.

#### Exceptions
##### Catch narrow exceptions
Sometimes when using try/catch blocks, it may be tempting to just `catch Exception`, `Error`,
or `Throwable` so you don't have to worry about what type was thrown.  This is usually a bad idea,
as you can end up catching more than you really wanted to deal with.  For example,
`catch Exception` would capture `NullPointerException`, and `catch Throwable` would capture
`OutOfMemoryError`.

```Java
// Bad.
//   - If a RuntimeException happens, the program continues rather than aborting.
try {
    storage.insertUser(user);
} catch (Exception e) {
    LOG.error("Failed to insert user.");
}

try {
    storage.insertUser(user);
} catch (StorageException e) {
    LOG.error("Failed to insert user.");
}
```

##### Try not to swallow exceptions
An empty `catch` block is usually a bad idea, as you have no signal of a problem.  Coupled with
[narrow exception](#catch-narrow-exceptions) violations, it's a recipe for disaster.  Try to
properly handle the exception whenever or wherever you can, unless you absolutely know the empty
`catch` is harmless.

##### Throw appropriate exception types
Let your API users obey [catch narrow exceptions](#catch-narrow-exceptions), don't throw Exception.
Even if you are calling another naughty API that throws Exception, at least hide that so it doesn't
bubble up even further.  You should also make an effort to hide implementation details from your
callers when it comes to exceptions.

```Java
// Bad.
//   - Caller is forced to catch Exception, trapping many unnecessary types of issues.
interface DataStore {
    String fetchValue(String key) throws Exception;
}

// Better.
//   - The interface leaks details about one specific implementation.
interface DataStore {
    String fetchValue(String key) throws SQLException, UnknownHostException;
}

// Good.
//   - A custom exception type insulates the user from the implementation.
//   - Different implementations aren't forced to abuse irrelevant exception types.
interface DataStore {
    String fetchValue(String key) throws StorageException;

    static class StorageException extends Exception {
        ...
    }
}
```

### Use newer/better libraries

#### StringBuilder over StringBuffer
[StringBuffer](http://docs.oracle.com/javase/7/docs/api/java/lang/StringBuffer.html) is thread-safe,
which is rarely needed.

#### List over Vector
`Vector` is synchronized, which is often unneeded.  When synchronization is desirable,
a [synchronized list](http://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedList(java.util.List))
can usually serve as a drop-in replacement for `Vector`.

### equals() and hashCode()
If you override one, you must implement both.
*See the equals/hashCode
[contract](http://docs.oracle.com/javase/7/docs/api/java/lang/Object.html#hashCode())*

`Objects.equal()` and `Objects.hashCode()` make it very easy to follow these contracts.

### TODOs

#### Leave TODOs
A TODO isn't a bad thing - it's signaling a future developer (possibly yourself) that a
consideration was made, but omitted for various reasons.  It can also serve as a useful signal when
debugging.

#### Leave no TODO unassigned
TODOs should have owners, otherwise they are unlikely to ever be resolved.

```Java
// Bad.
//   - TODO is unassigned.
// TODO: Implement request backoff.

// Good.
// TODO(John Doe): Implement request backoff.
```

#### Adopt TODOs
You should adopt an orphan if the owner has left the company/project, or if you make
modifications to the code directly related to the TODO topic.

### Obey the Law of Demeter ([LoD](http://en.wikipedia.org/wiki/Law_of_Demeter))
The Law of Demeter is most obviously violated by breaking the
[one dot rule](http://en.wikipedia.org/wiki/Law_of_Demeter#In_object-oriented_programming), but
there are other code structures that lead to violations of the spirit of the law.

#### In classes
Take what you need, nothing more.  The key idea is to defer assembly to the layers of the code
that know enough to assemble and instead just take the minimal interface you need to get your
work done.

```Java
// Bad.
//   - Weigher uses hosts and port only to immediately construct another object.
class Weigher {
private final double defaultInitialRate;

Weigher(Iterable<String> hosts, int port, double defaultInitialRate) {
    this.defaultInitialRate = validateRate(defaultInitialRate);
    this.weightingService = createWeightingServiceClient(hosts, port);
}
}

// Good.
class Weigher {
private final double defaultInitialRate;

Weigher(WeightingService weightingService, double defaultInitialRate) {
    this.defaultInitialRate = validateRate(defaultInitialRate);
    this.weightingService = checkNotNull(weightingService);
}
}
```

If you want to provide a convenience constructor, a factory method or an external factory
in the form of a builder you still can, but by making the fundamental constructor of a
Weigher only take the things it actually uses it becomes easier to unit-test and adapt as
the system involves.

#### In methods
If a method has multiple isolated blocks consider naming these blocks by extracting them
to helper methods that do just one thing.  Besides making the calling sites read less
like code and more like english, the extracted sites are often easier to flow-analyse for
human eyes.  The classic case is branched variable assignment.  In the extreme, never do
this:

```Java
void calculate(Subject subject) {
    double weight;
    if (useWeightingService(subject)) {
        try {
            weight = weightingService.weight(subject.id);
        } catch (RemoteException e) {
            throw new LayerSpecificException("Failed to look up weight for " + subject, e)
        }
    } else {
        weight = defaultInitialRate * (1 + onlineLearnedBoost);
    }

    // Use weight here for further calculations
}
```

Instead do this:

```Java
void calculate(Subject subject) {
    double weight = calculateWeight(subject);

    // Use weight here for further calculations
}

private double calculateWeight(Subject subject) throws LayerSpecificException {
    if (useWeightingService(subject)) {
        return fetchSubjectWeight(subject.id)
    } else {
        return currentDefaultRate();
    }
}

private double fetchSubjectWeight(long subjectId) {
    try {
        return weightingService.weight(subjectId);
    } catch (RemoteException e) {
        throw new LayerSpecificException("Failed to look up weight for " + subject, e)
    }
}

private double currentDefaultRate() {
    defaultInitialRate * (1 + onlineLearnedBoost);
}
```

A code reader that generally trusts methods do what they say can scan calculate
quickly now and drill down only to those methods where I want to learn more.

### Don't Repeat Yourself ([DRY](http://en.wikipedia.org/wiki/Don't_repeat_yourself))
For a more long-winded discussion on this topic, read
[here](http://c2.com/cgi/wiki?DontRepeatYourself).

#### Extract constants whenever it makes sense

#### Centralize duplicate logic in utility functions

### Avoid unnecessary code
#### Superfluous temporary variables.

```Java
// Bad.
//   - The variable is immediately returned, and just serves to clutter the code.
List<String> strings = fetchStrings();
return strings;

// Good.
return fetchStrings();
```

#### Unneeded assignment.

```Java
// Bad.
//   - The null value is never realized.
String value = null;
try {
    value = "The value is " + parse(foo);
} catch (BadException e) {
    throw new IllegalStateException(e);
}

// Good
String value;
try {
    value = "The value is " + parse(foo);
} catch (BadException e) {
    throw new IllegalStateException(e);
}
```
