Options for Process Monitoring in nested operators

Version A:

Collect the class hierarchy once and set up a counter for each involved class.
During execution, each class continually updates the associated counter (identified by the
contributing class).
From the current counter states, the total progress can be calculated any time
(e.g., by a free-running thread).
Pros: Counter update can be be done from inside loops. Easy to implement.
Cons: Update calls must identify themselves with the correct class (cannot be enforced).

Version B:
The terminal instance receives a 'reportProgress()' invocation and returns a pair of
values (curVal, maxVal), from which the local progress can be calculated.
The methods are invoked by reflection . The method must be private to avoid overriding.
Pros: Asynchronous, no unnecessary overhead (relevant?).
Cons: The naming of private methods cannot be enforced by an interface (but can be annotated).
	Progress status must be available in visible instance variables.


Version C:
Local field with lambda expression?

Version D: **** check****
Annotate two field variables: @ProgressCurrent, @ProgressMax. Read their
values asynchronously by reflection.