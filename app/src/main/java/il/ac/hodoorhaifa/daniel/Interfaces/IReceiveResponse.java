package il.ac.hodoorhaifa.daniel.Interfaces;

public interface IReceiveResponse {
    void serverSentResponse(Boolean allGood);

    void changeErrorMessage(String message, boolean setVisible);
}
