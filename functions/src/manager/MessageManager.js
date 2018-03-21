
exports.editLastMessage = (event, database) => {
  const message = event.data.val();
  if (typeof message.name === 'undefined') return;

  database.ref('/rooms/' + message.roomId)
          .update({
              'lastMessage': message.name + ": " + message.content
          });
}

exports.sendNotifications = (event, database, messaging) => {
  const message = event.data.val();
  if (typeof message.name === 'undefined') return;

  console.log("Received new message. Room: ", message.roomId, " - User: ", message.userId, " - ", message.content);

  database.ref('/rooms/' + message.roomId)
          .once('value')
          .then((snapshot) => {
              console.log("Promise result: ", snapshot.val());
              const room = snapshot.val();
              for (var userId in room.userIds) {
                  if (userId !== message.userId) {
                      database.ref('/users/' + userId + '/registrationTokens')
                              .once('value')
                              .then((snapshot) => {
                                  const registrationTokens = Object.keys(snapshot.val());
                                  return sendNotification(registrationTokens, message, room.name, messaging);
                              }).catch((error) => {
                                  console.log("Promise error: ", error);
                              });
                  }
              }
              return snapshot.val();
          }).catch((error) => {
              console.log("Promise error: ", error);
          });
}

function sendNotification(registrationTokens, message, roomName, messaging) {
    // Notification details.
    console.log("Tokens: ", registrationTokens);
    const payload = {
      notification: {
        title: roomName,
        body: `${message.name} sent a message.`,
        tag: message.roomId
      }
    };
    return messaging.sendToDevice(registrationTokens, payload)
                    .then((response) => {
                        console.log("Notification sent.", response);
                        return response;
                    })
                    .catch((error) => {
                        console.log("Notification could not be sent.", error);
                    });
}
