import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

public class CoAPClientArtifact extends Artifact{

    public void init() {}

    @OPERATION
    public void sendCoAPRequest(final String to, OpFeedbackParam<String> response) {
        


    }
    
}
