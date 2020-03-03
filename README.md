# RxPM

[ ![Download](https://api.bintray.com/packages/dmdev/maven/RxPM/images/download.svg) ](https://bintray.com/dmdev/maven/RxPM/_latestVersion)[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RxPM-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/7089)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Reactive implementation of [Presentation Model](https://martinfowler.com/eaaDev/PresentationModel.html) pattern in Android.

RxPM allows to use the RxJava all the way from the view to the model.  
The main advantage of that is the **ability to write UI logic declaratively**.

We focus on practice, so the library solves most of the typical presentation layer problems.

### Why PM and not MVVM?
Actually the only difference between these two is that PM does'n have automated binding.  
So PM name is just more correct for us. However many call it MVVM, so let it be.

### The Diagram
<img src="/docs/images/rxpm_diagram.png">

## Usage

Add the dependency to your build.gradle:
```gradle
dependencies {

    implementation 'me.dmdev.rxpm:rxpm:$latest_version'
    
    // RxBinding (optional)
    implementation 'com.jakewharton.rxbinding3:rxbinding:$latest_version'
    
}
```

### Create a Presentation Model class and define reactive properties
```kotlin
class CounterPm : PresentationModel() {

    companion object {
        const val MAX_COUNT = 10
    }

    val count = state(initialValue = 0)

    val minusButtonEnabled = state {
        count.observable.map { it > 0 }
    }

    val plusButtonEnabled = state {
        count.observable.map { it < MAX_COUNT }
    }

    val minusButtonClicks = action<Unit> {
        this.filter { count.value > 0 }
            .map { count.value - 1 }
            .doOnNext(count.consumer)
    }

    val plusButtonClicks = action<Unit> {
        this.filter { count.value < MAX_COUNT }
            .map { count.value + 1 }
            .doOnNext(count.consumer)
    }
}
```
In this sample the initialisation of states and actions is done in their own blocks, but it's also possible to do it in `onCreate()` or other callbacks. Don't forget to use `untilDestroy()` or other similar extension.
### Bind to the PresentationModel properties
```kotlin
class CounterActivity : PmActivity<CounterPm>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)
    }

    override fun providePresentationModel() = CounterPm()

    override fun onBindPresentationModel(pm: CounterPm) {

        pm.count bindTo { counterText.text = it.toString() }
        pm.minusButtonEnabled bindTo minusButton::setEnabled
        pm.plusButtonEnabled bindTo plusButton::setEnabled

        minusButton.clicks() bindTo pm.minusButtonClicks
        plusButton.clicks() bindTo pm.plusButtonClicks
    }
}
```

## Main Components

### PresentationModel
The PresentationModel stores the state of the View and holds the UI logic.  
PresentationModel instance is automatically retained during configuration changes. This behavior is provided by the delegate which controls the lifecycle.

Lifecycle callbacks:
- `onCreate()` — Called when the PresentationModel is created. Initialize your Rx chains in this method.
- `onBind()` — Called when the View binds to the PresentationModel.
- `onResume` - Called when the View resumes and begins to receive updates from states and commands.
- `onPause` - Called when the View pauses. At this point, states and commands stop emitting to the View and turn on internal buffer until the View resumes again.
- `onUnbind()` — Called when the View unbinds from the PresentationModel.
- `onDestroy()` — Called when the PresentationModel is being destroyed. Dispose all subscriptions in this method.

What's more, you can observe lifecycle changes via `lifecycleObservable`.

Also the useful extensions of the *Disposable* are available to make lifecycle handling easier: `untilPause`,`untilUnbind` and `untilDestroy`.

### PmView
The library has several predefined PmView implementations: `PmActivity`, `PmFragment`, `PmDialogFragment` and `PmController` (for [Conductor](https://github.com/bluelinelabs/Conductor/)'s users).  

You have to implement only two methods:
1) `providePresentationModel()` — Create the instance of the PresentationModel.
2) `onBindPresentationModel()` — Bind to the PresentationModel properties in this method. Use the `bindTo`, `passTo` extensions and [RxBinding](https://github.com/JakeWharton/RxBinding) to do this.

### State
**State** is a reactive property which represents a View state.  
It holds the latest value and emits it on binding. For example, **State** can be used to represent a progress of the http-request or some data that can change in time.

In the PresentationModel:
```kotlin
val inProgress = state(false)
```
Change the value:
```kotlin
inProgress.accept(true)
```
Observe changes in the View:
```kotlin
pm.inProgress bindTo progressBar.visibility()
```
Usually there is a data source already or the state is derived from other states. In this case, it’s convenient to describe this using lambda as shown below:
```kotlin
// Disable the button during the request
val buttonEnabled = state(false) {
    inProgress.observable.map { progress -> !progress }
}
```

In order to optimize the state update and to avoid unnecessary rendering on the view you can add a `DiffStrategy` in the `State`. By default, the `DiffByEquals` strategy is used. It's suitable for primitives and simple date classes, whereas `DiffByReference` is better to use for collections(like List).

### Action
**Action** is the reactive property which represents the user actions.  
It's mostly used for receiving events from the View, such as clicks.

In the View:
```kotlin
button.clicks() bindTo pm.buttonClicks
```

In the PresentationModel:
```kotlin
val buttonClicks = action<Unit>()

// Subscribe in onCreate
buttonClicks.observable
    .subscribe {
        // handle click
    }
    .untilDestroy()
```

#### Action initialisation block to avoid mistakes
Typically, some Action triggers an asynchronous operation, such as a request to backend. In this case, the rx-chain may throw an exception and app will crash. It's possible to handle errors in the subscribe block, but this is not enough. After the first failure, the chain will be terminated and stop processing clicks. Therefore, the correct handling involves the use of the `retry` operator and looks as follows:

```kotlin
val buttonClicks = action<Unit>()

// Subscribe in onCreate
buttonClicks.observable
    .skipWhileInProgress(inProgress) // filter clicks during the request
    .switchMapSingle {
        requestInteractor()
            .bindProgress(inProgress)
            .doOnSuccess { /* handle result */ }
            .doOnError { /* handel error */ }
    }
    .retry()
    .subscribe()
    .untilDestroy()
```
But often people forget about it. Therefore, we added the ability to describe the rx-chain of `Action` in it's initialisation block. This improves readability and eliminates boilerplate code:
```kotlin
val buttonClicks = action<Unit> {
    this.skipWhileInProgress(inProgress) // filter clicks during the request
        .switchMapSingle {
            requestInteractor()
                .bindProgress(inProgress)
                .doOnSuccess { /* handle result */ }
                .doOnError { /* handel error */ }
    }
}
```

### Command
**Command** is the reactive property which represents a command to the View.  
It can be used to show a toast or snackbar.

Define it in the PresentationModel:
```kotlin
val errorMessage = Command<String>()
```
Show some message in the View:
```kotlin
pm.errorMessage bindTo { message ->
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
```

When the View is paused, **Command** collects all received values and emits them on resume:

![Command](/docs/images/bwp.png)

## Controls

### Two-way Data Binding
For the cases of two-way data binding (eg. input field text changes) the library has predefined [Сontrols](https://github.com/dmdevgo/RxPM/tree/develop/rxpm/src/main/kotlin/me/dmdev/rxpm/widget).

In the PresentationModel:
```kotlin
val name = inputControl(
    formatter = {
        it.take(50).capitalize().replace("[^a-zA-Z- ]".toRegex(), "")
    }
)

val checked = checkControl()
```

In the View:
```kotlin
pm.name bindTo editText
pm.checked bindTo checkBox
```

### Dialogs
The DialogControl is a component make possible the interaction with the dialogs in reactive style.  
It manages the lifecycle and the state of the dialog. Just bind your Dialog object (eg. AlertDialog) to the DialogControl. No need in DialogFragment anymore.

Here is an example of the dialog to confirm exit from the application:
```kotlin
enum class DialogResult { EXIT, CANCEL }

val dialogControl = dialogControl<String, DialogResult>()

val backButtonClicks = action<Unit> {
    this.switchMapMaybe {
            dialogControl.showForResult("Do you really want to exit?")
        }
        .filter { it == DialogResult.EXIT }
        .doOnNext {
            // close application
        }
}
```

Bind the `dialogControl` to AlertDialog in the View:
```kotlin
pm.dialogControl bindTo { message, dialogControl ->
    AlertDialog.Builder(context)
        .setMessage(message)
        .setPositiveButton("Exit") { _, _ ->
            dialogControl.sendResult(DialogResult.EXIT)
        }
        .setNegativeButton("Cancel") { _, _ ->
            dialogControl.sendResult(DialogResult.CANCEL)
        }
        .create()
}
```

### Form Validation
Validating forms is now easy. Create the `FormValidator` using DSL to check `InputControls` and `CheckControls`:
```kotlin
val validateButtonClicks = action<Unit> {
    doOnNext { formValidator.validate() }
}
    
private val formValidator = formValidator {

    input(name) {
        empty("Input Name")
    }
    
    input(email, required = false) {
        pattern(ANDROID_EMAIL_PATTERN, "Invalid e-mail address")
    }
    
    input(phone, validateOnFocusLoss = true) {
        valid(phoneUtil::isValidPhone, "Invalid phone number")
    }
    
    input(password) {
        empty("Input Password")
        minSymbols(6, "Minimum 6 symbols")
        pattern(
            regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]).{6,}\$",
            errorMessage = "The password must contain a large and small letters, numbers."
        )
    }
    
    input(confirmPassword) {
        empty("Confirm Password")
        equalsTo(password, "Passwords do not match")
    }
    
    check(termsCheckBox) {
        acceptTermsOfUse.accept("Please accept the terms of use")
    }
}
```

## Paging and Loading
In almost every application, there are pagination and data loading. What's more, we have to handle screen states correctly.
We recommend using the library [RxPagingLoading](https://github.com/MobileUpLLC/RxPagingLoading). The solution is based on the usage of [Unidirectional Data Flow](https://en.wikipedia.org/wiki/Unidirectional_Data_Flow_(computer_science)) pattern and is perfectly compatible with RxPM.
## Sample

The [sample](https://github.com/dmdevgo/RxPM/tree/develop/sample) shows how to use RxPM in practice.

## How to test PM?

You can test PresentationModel in the same way as any other class with RxJava (using TestObserver, Mockito, other).  
The only difference is that you have to change it's lifecycle state while testing. And **PmTestHelper** allows you to do that.

Note that Command passes events only when PM is in the RESUMED state.

## License

```

MIT License

Copyright (c) 2017 Dmitriy Gorbunov (dmitriy.goto@gmail.com)
                   and Vasili Chyrvon (vasili.chyrvon@gmail.com)

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
