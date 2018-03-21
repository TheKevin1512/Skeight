
exports.removeRoomMessages = (event, database) => {
  return database.ref('/messages')
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
}

exports.removeRoom = (event, database) => {
  const roomId = event.params.roomId;

  return database.ref('/rooms/' + roomId)
                 .once('value')
                 .then((snapshot) => {
                    snapshot.ref.remove();
                    return snapshot;
                 })
                 .catch((error) => {
                    return error;
                 });
}

exports.joinRoom = (event, database) => {
  const roomId = event.params.roomId;
  const userId = event.params.userId;

  return database.ref('/users/' + userId + '/name')
                 .once('value')
                 .then((snapshot) => {
                    const name = snapshot.val();
                    const message = {
                      content: name + " has joined the room.",
                      roomId: roomId
                    };
                    return database.ref('/messages').push(message);
                 })
                 .catch((error) => {
                    console.log("Promise error: ", error);
                 });
}

exports.leaveRoom = (event, database) => {
  const roomId = event.params.roomId;
  const userId = event.params.userId;

  return database.ref('/users/' + userId + '/name')
                 .once('value')
                 .then((snapshot) => {
                    const name = snapshot.val();
                    const message = {
                      content: name + " has left the room.",
                      roomId: roomId
                    };
                    return database.ref('/messages').push(message);
                 })
                 .catch((error) => {
                    console.log("Promise error: ", error);
                 });
}
