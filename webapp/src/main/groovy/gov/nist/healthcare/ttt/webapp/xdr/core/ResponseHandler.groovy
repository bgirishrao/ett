package gov.nist.healthcare.ttt.webapp.xdr.core
import gov.nist.healthcare.ttt.database.xdr.XDRRecordInterface
import gov.nist.healthcare.ttt.database.xdr.XDRReportItemImpl
import gov.nist.healthcare.ttt.webapp.xdr.domain.testcase.TestCaseStrategy
import gov.nist.healthcare.ttt.xdr.api.notification.IObserver
import gov.nist.healthcare.ttt.xdr.domain.Message
import gov.nist.healthcare.ttt.xdr.domain.TkValidationReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Created by gerardin on 10/14/14.
 */
@Component
class ResponseHandler implements IObserver{


    private final TestCaseManager manager

    @Autowired
    public ResponseHandler(TestCaseManager manager){
        this.manager = manager
        manager.receiver.registerObserver(this)
    }

    @Override
    def getNotification(Message msg) {

        println "notification received"

        try {
            handle(msg.content)
        }
        catch(Exception e){
            e.printStackTrace()
            println "notification content not understood"
        }
    }


    private handle(TkValidationReport report){



        TestCaseStrategy testcase = manager.findTestCase(id)
        testcase.notifyXdrReceive(rec,step,report)
    }
}
