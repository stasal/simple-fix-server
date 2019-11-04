package ru.stasal.fix

import io.allune.quickfixj.spring.boot.starter.model.FromApp
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import quickfix.Session
import quickfix.field.*
import quickfix.fix50.ExecutionReport
import quickfix.fix50.NewOrderSingle
import quickfix.fix50.OrderCancelReplaceRequest
import quickfix.fix50.OrderCancelRequest

@Component
class IncomingMessageHandler {

    @EventListener
    fun fromApp(fromApp: FromApp) {
        when (fromApp.message) {
            is NewOrderSingle -> {
                onNewOrderSingle(fromApp)
            }
            is OrderCancelRequest -> {
                onOrderCancelRequest(fromApp)
            }
            is OrderCancelReplaceRequest -> {
                onOrderCancelReplaceRequest(fromApp)
            }
        }
    }

    private fun onOrderCancelReplaceRequest(fromApp: FromApp) {
        val ocrr = fromApp.message as OrderCancelReplaceRequest
        Session.sendToTarget(
            ExecutionReport(
                OrderID(System.nanoTime().toString()),
                ExecID(System.nanoTime().toString()),
                ExecType(ExecType.PENDING_REPLACE),
                OrdStatus(OrdStatus.PENDING_REPLACE),
                Side(ocrr.side.value),
                LeavesQty(ocrr.orderQty.value),
                CumQty(0.0)
            ).apply {
                set(ocrr.clOrdID)
                set(ocrr.securityID)
                set(ocrr.price)
                set(ocrr.orderQty)
            },
            fromApp.sessionId
        )
        Session.sendToTarget(
            ExecutionReport(
                OrderID(System.nanoTime().toString()),
                ExecID(System.nanoTime().toString()),
                ExecType(ExecType.REPLACED),
                OrdStatus(OrdStatus.REPLACED),
                Side(ocrr.side.value),
                LeavesQty(ocrr.orderQty.value),
                CumQty(0.0)
            ).apply {
                set(ocrr.clOrdID)
                set(OrigClOrdID(ocrr.clOrdID.value))
                set(ocrr.securityID)
                set(ocrr.price)
                set(ocrr.orderQty)
                setField(ocrr.exDestination)
            },
            fromApp.sessionId
        )
    }

    private fun onNewOrderSingle(fromApp: FromApp) {
        val nos = fromApp.message as NewOrderSingle
        Session.sendToTarget(
            ExecutionReport(
                OrderID(System.nanoTime().toString()),
                ExecID(System.nanoTime().toString()),
                ExecType(ExecType.NEW),
                OrdStatus(OrdStatus.NEW),
                Side(nos.side.value),
                LeavesQty(nos.orderQty.value),
                CumQty(0.0)
            ).apply {
                set(nos.clOrdID)
                set(OrigClOrdID(nos.clOrdID.value))
                set(nos.securityID)
                set(nos.price)
                set(nos.orderQty)
                setField(nos.exDestination)
            },
            fromApp.sessionId
        )
    }

    private fun onOrderCancelRequest(fromApp: FromApp) {
        val ocr = fromApp.message as OrderCancelRequest
        Session.sendToTarget(
            ExecutionReport(
                OrderID(System.nanoTime().toString()),
                ExecID(System.nanoTime().toString()),
                ExecType(ExecType.CANCELED),
                OrdStatus(OrdStatus.CANCELED),
                Side(ocr.side.value),
                LeavesQty(ocr.orderQty.value),
                CumQty(0.0)
            ).apply {
                set(ocr.clOrdID)
                set(ocr.origClOrdID)
            },
            fromApp.sessionId
        )
    }
}