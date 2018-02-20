'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.editLastMessage = functions.database.ref(`/messages/{messageId}`).onWrite((event) => {
  // Only edit data when it is first created.
  if (event.data.previous.exists()) {
    return;
  }
  const message = event.data.val();
  return admin.database()
              .ref('/rooms/' + message.roomId)
              .update({
                  'lastMessage': message.name + ": " + message.content
              });
});

exports.sendMessageNotification = functions.database.ref(`/messages/{messageId}`).onWrite((event) => {
  // Only edit data when it is first created.
  if (event.data.previous.exists()) {
    return;
  }
  const message = event.data.val();
  console.log("Received new message. Room: ", message.roomId, " - User: ", message.userId, " - ", message.content);

  const getRoomPromise = admin.database()
                              .ref('/rooms/' + message.roomId)
                              .once('value')
                              .then((snapshot) => {
                                  console.log("Promise result: ", snapshot.val());
                                  for (var userId in snapshot.val().userIds) {
                                      if (userId !== message.userId) {
                                          admin.database()
                                            .ref('/users/' + userId + '/registrationTokens')
                                            .once('value')
                                            .then((snapshot) => {
                                              const registrationTokens = Object.keys(snapshot.val());
                                              return sendNotification(registrationTokens, message);
                                            }).catch((error) => {
                                              console.log("Promise error: ", error);
                                            });
                                      }
                                  }
                                  return snapshot.val();
                              }).catch((error) => {
                                  console.log("Promise error: ", error);
                              });
});

function sendNotification(registrationTokens, message) {
    // Notification details.
    console.log("Tokens: ", registrationTokens);
    const payload = {
      notification: {
        title: 'Skeight',
        body: `${message.name} sent a message.`
      }
    };
    return admin.messaging().sendToDevice(registrationTokens, payload)
                            .then((response) => {
                                console.log("Notification sent.", response);
                                return response;
                            })
                            .catch((error) => {
                                console.log("Notification could not be sent.", error);
                            })
}
