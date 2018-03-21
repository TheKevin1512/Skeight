'use strict';

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const messageManager = require('./src/manager/MessageManager');
const roomManager = require('./src/manager/RoomManager');

exports.editLastMessage
  = functions.database.ref(`/messages/{messageId}`)
                      .onCreate((event) => {
                        return messageManager.editLastMessage(event, admin.database());
                      });

exports.sendNotifications
  = functions.database.ref(`/messages/{messageId}`)
                      .onCreate((event) => {
                        return messageManager.sendNotifications(event, admin.database(), admin.messaging());
                      });

exports.removeRoomMessages
  = functions.database.ref(`/rooms/{roomId}`)
                      .onDelete((event) => {
                        return roomManager.removeRoomMessages(event, admin.database());
                      });

exports.removeRoom
  = functions.database.ref(`/rooms/{roomId}/userIds`)
                      .onDelete((event) => {
                        return roomManager.removeRoom(event, admin.database());
                      });

exports.joinRoom
  = functions.database.ref(`/rooms/{roomId}/userIds/{userId}`)
                      .onCreate((event) => {
                        return roomManager.joinRoom(event, admin.database());
                      });

exports.leaveRoom
  = functions.database.ref(`/rooms/{roomId}/userIds/{userId}`)
                      .onDelete((event) => {
                        return roomManager.leaveRoom(event, admin.database());
                      });
