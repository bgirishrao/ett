package gov.nist.healthcare.ttt.webapp.xdr.domain.testcase.edge.receive
import gov.nist.healthcare.ttt.database.xdr.XDRRecordInterface
import gov.nist.healthcare.ttt.database.xdr.XDRTestStepInterface
import gov.nist.healthcare.ttt.tempxdrcommunication.artifact.ArtifactManagement
import gov.nist.healthcare.ttt.webapp.xdr.core.TestCaseExecutor
import gov.nist.healthcare.ttt.webapp.xdr.domain.MsgLabel
import gov.nist.healthcare.ttt.webapp.xdr.domain.TestCaseBuilder
import gov.nist.healthcare.ttt.webapp.xdr.domain.TestCaseEvent
import gov.nist.healthcare.ttt.webapp.xdr.domain.testcase.StandardContent
import gov.nist.healthcare.ttt.webapp.xdr.domain.testcase.TestCase
import gov.nist.healthcare.ttt.xdr.domain.TkValidationReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
/**
 * Created by gerardin on 10/27/14.
 */
@Component
final class TestCase3 extends TestCase {

    @Autowired
    public TestCase3(TestCaseExecutor executor) {
        super(executor)
    }


    @Override
    TestCaseEvent configure(Map context, String username) {

        def config = new HashMap()
        config.type = 'docsrc'
        config.endpoint = context.targetEndpoint
        sim = registerEndpoint(id, config)

        executor.createRecordForTestCase(context,username,id,sim)


        context.directTo = "testcase3@nist.gov"
        context.directFrom = "testcase3@nist.gov"
        context.wsaTo = context.targetEndpoint
        context.messageType = ArtifactManagement.Type.XDR_MINIMAL_METADATA

        context.simId = sim.simulatorId
        //This has no sense
        context.tls = "false"
        context.endpoint = sim.endpoint


        XDRTestStepInterface step = executor.executeSendXDRStep2(context)

        //Create a new test record.
        XDRRecordInterface record = new TestCaseBuilder(id, username).addStep(step).build()

        executor.db.addNewXdrRecord(record)

        //at this point the test case status is either PASSED or FAILED depending on the result of the validation
        XDRRecordInterface.CriteriaMet testStatus = done(step.criteriaMet, record)


        log.info(MsgLabel.XDR_SEND_AND_RECEIVE.msg)

        def content = new StandardContent()
        return new TestCaseEvent(XDRRecordInterface.CriteriaMet.PENDING, content)
    }

    @Override
    public void notifyXdrReceive(XDRRecordInterface record, TkValidationReport report) {

        XDRTestStepInterface step = executor.executeStoreXDRReport(report)
        XDRRecordInterface updatedRecord = new TestCaseBuilder(record).addStep(step).build()

        //TODO for now it is a manual check
        done(XDRRecordInterface.CriteriaMet.MANUAL, updatedRecord)

    }


}
