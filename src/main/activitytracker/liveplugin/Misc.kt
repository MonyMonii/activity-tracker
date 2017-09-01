package activitytracker.liveplugin

import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import java.util.concurrent.atomic.AtomicBoolean

fun Disposable.createChild() = newDisposable(listOf(this), {})

fun Disposable.whenDisposed(callback: () -> Any) = newDisposable(listOf(this), callback)

fun newDisposable(vararg parents: Disposable, callback: () -> Any = {}) = newDisposable(parents.toList(), callback)

fun newDisposable(parents: Collection<Disposable>, callback: () -> Any = {}): Disposable {
    val isDisposed = AtomicBoolean(false)
    val disposable = Disposable {
        if (!isDisposed.get()) {
            isDisposed.set(true)
            callback()
        }
    }
    parents.forEach { parent ->
        // can't use here "Disposer.register(parent, disposable)"
        // because Disposer only allows one parent to one child registration of Disposable objects
        Disposer.register(parent, Disposable {
            Disposer.dispose(disposable)
        })
    }
    return disposable
}
