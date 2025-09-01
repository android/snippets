import Foundation
import KmpKit
import SwiftUI

// [START android_kmp_viewmodel_ios_contentview]
struct ContentView: View {

    /// Use the store owner as a StateObject to allow retrieving ViewModels and scoping it to this screen.
    @StateObject private var viewModelStoreOwner = IosViewModelStoreOwner()

    var body: some View {
        /// Retrieves the `MainViewModel` instance using the `viewModelStoreOwner`.
        /// The `MainViewModel.Factory` and `creationExtras` are provided to enable dependency injection
        /// and proper initialization of the ViewModel with its required `AppContainer`.
        let mainViewModel: MainViewModel = viewModelStoreOwner.viewModel(
            factory: MainViewModelKt.mainViewModelFactory
        )
        // [START_EXCLUDE]
        VStack(spacing: 16) {
            Image(systemName: "swift")
                .font(.system(size: 200))
                .foregroundColor(.accentColor)
            Text("SwiftUI")
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .top)
        .padding()
        // [END_EXCLUDE]
        // .. the rest of the SwiftUI code
    }
}
// [END android_kmp_viewmodel_ios_contentview]
