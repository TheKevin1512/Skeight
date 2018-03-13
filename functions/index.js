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
  if (typeof message.name === 'undefined') {
    return;
  }
  return admin.database()
              .ref('/rooms/' + message.roomId)
              .update({
                  'lastMessage': message.name + ": " + message.content
              });
});

exports.removeRoomMessages = functions.database.ref(`/rooms/{roomId}`).onDelete((event) => {
  return admin.database()
              .ref('/messages')
              .orderByChild('roomId')
              .equalTo(event.params.roomId)
              .once('value')
              .then((snapshot) => {
                snapshot.ref.remove();
                return snapshot;
              })
              .catch((error) => {
                return error;
              });
});

exports.removeRoom = functions.database.ref(`/rooms/{roomId}/userIds`).onDelete((event) => {
  const roomId = event.params.roomId;
  return admin.database()
              .ref('/rooms/' + roomId)
              .once('value')
              .then((snapshot) => {
                snapshot.ref.remove();
                return snapshot;
              })
              .catch((error) => {
                return error;
              });
});

exports.joinRoom = functions.database.ref(`/rooms/{roomId}/userIds/{userId}`).onWrite((event) => {
  // Only edit data when it is first created.
  if (event.data.previous.exists()) {
    return;
  }
  const roomId = event.params.roomId;
  const userId = event.params.userId;

  return admin.database()
              .ref('/users/' + userId + '/name')
              .once('value')
              .then((snapshot) => {
                  const name = snapshot.val();
                  const message = {
                      content: name + " has joined the room.",
                      roomId: roomId
                  };
                  return admin.database()
                              .ref('/messages')
                              .push(message);
              }).catch((error) => {
                  console.log("Promise error: ", error);
              });
});

exports.leftRoom = functions.database.ref(`/rooms/{roomId}/userIds/{userId}`).onDelete((event) => {
  const roomId = event.params.roomId;
  const userId = event.params.userId;

  return admin.database()
              .ref('/users/' + userId + '/name')
              .once('value')
              .then((snapshot) => {
                  const name = snapshot.val();
                  const message = {
                      content: name + " has left the room.",
                      roomId: roomId
                  };
                  return admin.database()
                              .ref('/messages')
                              .push(message);
              }).catch((error) => {
                  console.log("Promise error: ", error);
              });
});

exports.sendMessageNotification = functions.database.ref(`/messages/{messageId}`).onWrite((event) => {
  // Only edit data when it is first created.
  if (event.data.previous.exists()) {
    return;
  }
  const message = event.data.val();
  if (typeof message.name === 'undefined') {
    return;
  }
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
        body: `${message.name} sent a message.`,
        tag: message.roomId
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
