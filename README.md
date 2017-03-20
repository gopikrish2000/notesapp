# notesapp
This is a sample Notes Application which has features like add a note , delete , search etc . Done with latest concepts like Rxjava ...

The following are the things done in the Notes App.

**** Components Used/Done in this project. ***
1. Used Latest Android SDK 24 ( which this app is currently targeting as well ) and all build tools etc using are updated ones.
2. Using Java8 with jackoptions which will enable Lambda expressions, method references etc.
3. Used RxJava, RxAndroid extensively to make Database calls in Background and call backs in the Main thread with unsubscription in the onDestroy.
4. Used RxBinding for binding view clicks, editText text changes event etc with throttleFirst and debounce , so that so avoid the multiple clicks
   /multiple events getting fired in a span of a second.
5. Using Design for Floating ActionButton.
6. Using PublishSubjects instead of interfaces for better code readability and handling of subscriptions.
6. Used MVP pattern in the code , to segregated UI part from the logic with Presenters.


**** Features included in this project. Most of them i added more than which is provided in the Document ***
1. Added Search Features to search items.
2. Added Add item, Delete item ( in two places Listing Page long press and Details page menu click ), update item.
3. Optimized on Item addition/deleting/modification , Only updating that item in Recyclerview. Also Optimizing on Database calls upon
   addition/deleting/modification by passing the respective item and avoiding the extra Database call.
4. Saving list in onSaveInstanceState so that on mobile flip again Database call can be avoided.
5. All Database calls in Background thread using RxJava to prevent UI lags.
6. Using RxBinding for view clicks, editText text changes event etc with throttleFirst and debounce , so that so avoid the multiple clicks
      /multiple events getting fired in a span of a second.
7. Auto Save option in Details page. So on back click will also save the item.
8. Title of notes is picked from the first non empty line entered in description by User ( provided if title is not entered by user).
9. Showing Modified Time and Time difference information in the Notes based on timezone.

10. In Detailspage double tap to edit ( also user can click on edit button below)
11. In Listing page showing description with empty lines removed. So that user can see lot of information in notes.
12. Every time ( even after adding/deletion/modification) the items in listing page are always sorted by time in descending order.
13. Delete confirmation Dialog for confirming with the user to delete the item.
14. Doing only Soft delete of the items ( isDeleted = 1 ). So that we can show these list of items in new Trash Page.
15. Done other features like showing progressbar while loading, publishsubjects instead of interfaces etc.


**** UI Features Added ******
1. Added Floating Action Button for Add.
2. Added Ripple Effects for clicks and long press for all buttons and listing page notes item.
3. Using custom EditText with lines drawn in the Details page.
