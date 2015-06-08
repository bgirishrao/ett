package gov.nist.healthcare.ttt.webapp.xdr.domain.testcase.edge.send.mu2
import gov.nist.healthcare.ttt.database.xdr.XDRRecordInterface
import gov.nist.healthcare.ttt.database.xdr.XDRTestStepInterface
import gov.nist.healthcare.ttt.webapp.xdr.core.TestCaseExecutor
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
final class TestCase50a extends TestCase {

    final public String goodEndpoint = id

    @Autowired
    public TestCase50a(TestCaseExecutor ex) {
        super(ex)
    }

    @Override
    TestCaseEvent configure(Map context, String username) {

        executor.createRecordForTestCase(context,username,id,sim)

        log.info "test case ${id} : successfully configured. Ready to receive messages."

        def content = new StandardContent()
        content.endpoint = endpoints[0]
        content.endpointTLS = endpoints[1]
        return new TestCaseEvent(XDRRecordInterface.CriteriaMet.MANUAL, content)
    }

    @Override
    public void notifyXdrReceive(XDRRecordInterface record, TkValidationReport report) {

        XDRTestStepInterface step

        step = executor.executeSendProcessedMDN(report)

        record = new TestCaseBuilder(record).addStep(step).build()

        record.criteriaMet = step.criteriaMet

        executor.db.updateXDRRecord(record)

        done(XDRRecordInterface.CriteriaMet.MANUAL, record)

    }
}
