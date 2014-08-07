# LibFX - Nestings

This feature branch revolves around JavaFX properties. The core API provides awesome capabilities but one thing I frequently need is missing: the possibility to interact with properties which are hidden in a more complex object hierarchy.

Below you will find an example of the problem and its solution with some lines of code from **LibFX**. But before we come to that I wanna shortly present this feature's idea.

## Idea

This branch develops a simple and fluent API to create nestings, where a `Nesting` represents a hierarchy like `Employee -> Address -> ZIP code`. The nesting would collapse the hierarchy to the innermost property (in this example `zipProperty`) and update itself whenever the employee or address instances change.

A nesting can than be used in several ways:
* You can build a property which always holds the innermost value.
* You can attach a change listener which is carried along as the innermost property changes.
* You can create bindings which are not only updated when the innermost property's value changes but also when the property itself is replaced.

These further steps can be made with the same fluent API without breaking one's stride. You can find an example below and more in the classes in the folder `demo/org/codefx/libfx/nesting` (look for methods starting with "demo").

## Example

Let's see an example...

### The Situation

Say you have an object of type `Employee` and you're creating an `EmployeeEditor` for editing a single employee at a time. You will most likely have a model for your UI which has something like a `currentEmployeeProperty`.
Now you might want to bind some properties of the controls you're using for your editor to the current employee's properties. For example you might have a slider and want to bind it to the employee's `salaryProperty`.

### The Problem

Up to now that's all straight forward. But what happens when the current employee is replaced by another? Of course you want your editor to be updated.

### The "Solutions"

You could use `Bindings.select` but it has some downsides. For one thing, it uses strings to identify the nested properties, which breaks down quickly under refactoring. Unfortunately you won't even get an exception when trying to access properties which aren't there anymore - your binding will just forever contain null. Another downside is the return type. It's just an `ObjectBinding` (or `DoubleBinding` or ...) which does not suffice in all use cases.

Another way is to explicitly listen to changes of the model's `currentEmployeeProperty` and update the binding accordingly. That's rather tedious and leads to a lot of the same code all over the place. And it gets even worse, when you're nesting deeper, e.g. binding to the current employee's address' ZIP code.

### The Solution

Use **LibFX**! :)

``` Java
Nestings.on(currentEmployeeProperty)
	.nestDouble(employee -> employee.salaryProperty())
	.bindBidirectional(slider.valueProperty());
```

``` Java
Nestings.on(currentEmployeeProperty)
	.nest(employee -> employee.addressProperty())
	.nest(address -> address.zipProperty())
	.addChangeListener(myListener);
```

