const { onValueCreated } = require("firebase-functions/v2/database");
const { initializeApp } = require("firebase-admin/app");
const { getMessaging } = require("firebase-admin/messaging");
const { getDatabase } = require("firebase-admin/database");

initializeApp();

// Se dispara cuando se crea una nueva compra en Firebase
exports.notificarCompra = onValueCreated(
  "compras/{uid}/{compraId}",
  async (event) => {
    const uid = event.params.uid;
    const compra = event.data.val();

    if (!compra) return null;

    try {
      // Obtener el token FCM del usuario
      const db = getDatabase();
      const tokenSnapshot = await db
        .ref(`fcm_tokens/${uid}`)
        .get();

      const token = tokenSnapshot.val();
      if (!token) {
        console.log(`No hay token FCM para uid: ${uid}`);
        return null;
      }

      // Enviar notificación push al dispositivo
      const mensaje = {
        token: token,
        notification: {
          title: "¡Compra confirmada! 🎫",
          body: `Tu boleto para ${compra.nombreEvento} ha sido procesado`,
        },
        data: {
          titulo: "¡Compra confirmada! 🎫",
          cuerpo: `Tu boleto para ${compra.nombreEvento} ha sido procesado`,
          eventoId: compra.eventoId ?? "",
          nombreEvento: compra.nombreEvento ?? "",
        },
        android: {
          priority: "high",
          notification: {
            channelId: "sweeptickets_fcm",
            sound: "default",
          },
        },
      };

      const response = await getMessaging().send(mensaje);
      console.log(`Notificación enviada exitosamente: ${response}`);
      return null;

    } catch (error) {
      console.error(`Error enviando notificación: ${error}`);
      return null;
    }
  }
);