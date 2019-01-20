# FlickrClient
An Android app for showing photos from Flickr based on search queries.

The architecture of this app has been divided into three layers - UI, ViewModels and Services.

The UI layer contains activities and fragments for showing the views and is also responsible for decoding the photos. 
The views in the UI layer observe changes in the ViewModels using LiveData objects.

ViewModels are implemented using the ViewModel architecture component. 
They listen for events in the Service layer using interface callbacks.

The Service layer is mainly responsible for fetching search results and downloading photos. 
A factory is created for vending service objects that helps with unit-testing as well.
