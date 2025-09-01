import Foundation
import KmpKit

// [START android_kmp_viewmodel_ios_viewmodel_storeowner]
class IosViewModelStoreOwner: ObservableObject, ViewModelStoreOwner {

    let viewModelStore = ViewModelStore()

    /// This function allows retrieving the androidx ViewModel from the store.
    /// It uses the utilify function to pass the generic type T to shared code
    func viewModel<T: ViewModel>(
        key: String? = nil,
        factory: ViewModelProviderFactory,
        extras: CreationExtras? = nil
    ) -> T {
        do {
            return try viewModelStore.resolveViewModel(
                modelClass: T.self,
                factory: factory,
                key: key,
                extras: extras
            ) as! T
        } catch {
            fatalError("Failed to create ViewModel of type \(T.self)")
        }
    }

    /// This is called when this class is used as a `@StateObject`
    deinit {
        viewModelStore.clear()
    }
}
// [END android_kmp_viewmodel_ios_viewmodel_storeowner]
