package com.atlas.controlefinanceiro

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import java.util.Locale

class BankNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val full = "$title $text".lowercase(Locale("pt", "BR"))

        // Só considera saídas explicitamente descritas como Pix/transferência enviada/realizada.
        val looksLikeOutgoing = (full.contains("pix") || full.contains("transfer")) &&
            listOf("enviado", "enviada", "realizado", "realizada", "pagamento", "você pagou", "voce pagou").any { full.contains(it) }
        if (!looksLikeOutgoing) return

        val match = Regex("(?:r\\$\\s*)?(\\d{1,3}(?:\\.\\d{3})*,\\d{2}|\\d+[.,]\\d{2})").find(full) ?: return
        val raw = match.groupValues[1].replace(".", "").replace(",", ".")
        val amount = raw.toDoubleOrNull()?.takeIf { it > 0 } ?: return

        val prefs = getSharedPreferences("gastos_sarah", MODE_PRIVATE)
        val uniqueKey = "${sbn.packageName}:${sbn.id}:${sbn.postTime}:$amount"
        if (prefs.getString("last_auto_tx", "") == uniqueKey) return

        val current = prefs.getFloat("account_balance", 1326.40f).toDouble()
        prefs.edit()
            .putFloat("account_balance", (current - amount).toFloat())
            .putString("last_auto_tx", uniqueKey)
            .putString("last_auto_description", "$title • $text")
            .putFloat("last_auto_amount", amount.toFloat())
            .apply()
    }
}
