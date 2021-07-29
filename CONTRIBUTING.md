# How to contribute

If you want to contribute to the NMF, follow these guidelines to get your code as easily through the review as possible.

## Commit messages

1. The title: Use present tense imperative to shortly (ideally less than 50 characters) describe your changes. You may reference an issue to further clarify which problem(s) your commit addresses.

Furthermore, do not end the title with a period, as titles do not end with periods. A valid title would be: `Bug #42 [api] Fix camera snapshot method`.
2. Leave a blank line after your title.
3. Include a full link to the issue.
4. The body: Try to keep a column limit of around 72 characters to avoid horizontal scrolling. Use the body to further elaborate your changes using present tense imperative. You may name further TODOs if you referenced an issue and did not solve the problem completely. A fully valid commit message could look like this:
```
Bug #42 [api] Fix camera snapshot method

https://gitlab.com/esa/NMF/nmf-issues/issues/42

Fix the method CameraAPI.snapshot() so the experimenter
can take single images.
TODO:
* Implement the CameraAPI.getImageStream() method
* Document the API and write tests
```
5. Remember to keep your commit messages as precise as possible.

## Coding style

Consistency is crucial to good collaboration between different contributors. For this purpose we enforce the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
Please note that the XML specification of services results in the generation of Java classes and methods. Therefore, the names of services are supposed to be written as UpperCamelCase and the service
parameters are supposed to be written in lowerCamelCase.

## Quality assurance (QA)

The NMF is supposed to operate on Cubesats which are associated with a lot of work by dedicated teams. To ensure that their time and work is not wasted by erronous software, everyone contributing to the NMF shall try to keep the framework as usable and correct as possible.

### Documentation

Good documentation helps new users to implement their vision into code and also helps the reviewer to understand the intention behind your contribution. Furthermore, good documentation can be used to write
tests and spot problems with your code. For the NMF we use Javadoc to document classes and methods.

#### Classes

1. Document your new classes with an @author tag, so we know who to contact if problems arise in the future.
2. Briefly describe the purpose of your class.

#### Methods

1. Every method gets a Javadoc docstring. Make sure to use the first line for a brief summary (see [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html#s7-javadoc) for more
information).
2. Describe precisely what your method accomplishes.
3. Explain your parameters. Not everyone has the same amount of experience, so explain how each parameter affects the computation. This will assist new users and also testers who want to find sensible test cases.
4. Be precise about your return values. State clearly if null is an acceptable return value and the cases when this will occur.
5. Explain precisely when exceptions are thrown.
6. Describe what your method accomplishes, not how it is accomplished.
A rule of thumb is: Could another developer use your documentation to write tests for your method without looking at your code? If not, be more specific.

### Testing

When writing new public methods we expect that these methods are tested on different representive use cases. The minimum requirements for test-cases are:

1. null valued parameters in every possible combination.
2. MIN\_VALUE and MAX\_VALUE tests in every possible combination.
3. Neutral elements as parameter values in at least one combination (neutral elements are: 0 for addition, 1 for multiplication, \"\" for string concatenation, etc.).
4. Two test cases with some arbitrary values in the parameters.
5. If the method is specified to throw exceptions, try to force each exception with at least one test.
Remember: You want to break your code with tests, so don't just try to enter test-cases which are directly known to work. Your tests will later be used in the CI-pipeline.
