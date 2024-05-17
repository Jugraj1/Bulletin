/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */


// The Cloud Functions for Firebase SDK to create Cloud Functions and triggers.

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");




const functions = require('firebase-functions');
const admin = require('firebase-admin');

initializeApp();

// Old code used to create user document in firestore when created in auth. Abandoned since client isnt waiting for the cloud function to finish, before loading the user.
// Better do it all locally to avoid these issues.

// exports.onAuthCreateAccount = functions.auth.user().onCreate((user) => {

//   var userObject = {
//      email : user.email,
//   };

//   return admin.firestore().doc('users/'+user.uid).set(userObject);
//  // or admin.firestore().doc('users').add(userObject); for auto generated ID 

// });

exports.onPostUpdate = functions.firestore
    .document('posts/{postId}')
    .onUpdate((change, context) => {

        const newVal = change.after.data();
        const prevVal = change.before.data();
        const epoch = admin.firestore.Timestamp.fromDate(new Date('January 11, 2024 00:00:00')) // use firebase format so we can do easy subtraction
        // Let epoch be the timestamp of Jan 11, 2024 (Noah's birthday :) )
      //   functions.logger.info("EPOCH TIMESTAMP: ", epoch);
      //   functions.logger.info("POST TIMESTAMP: ", newVal.timeStamp);
        functions.logger.info("LIKE COUNT: ", newVal.likes.length);
        if (newVal.likes !== prevVal.likes) {
            return change.after.ref.update({
                score: (Math.log10(10*newVal.likes.length + (newVal.timeStamp - epoch)/259200)) // reddit formula of f(nlikes, timestamp) = log10(likes + (Timestamp - Epoch)/259200)
            });

        } else {
            return null;
        }

    });

exports.onCreatePost = functions.firestore
.document('posts/{postId}')
.onCreate((snapshot, context) => {
   const data = snapshot.data();
   const epoch = admin.firestore.Timestamp.fromDate(new Date('January 11, 2024 00:00:00'));
   return snapshot.ref.update({
      score: Math.log10(10 * data.likes.length + (data.timeStamp - epoch) / 259200)
   });
});


