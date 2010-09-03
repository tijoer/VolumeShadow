package volumeshadow.shader;

public class LinkerException extends Exception{
    byte[] infolog;

    public LinkerException(byte[] infolog){
        this.infolog = infolog;
    }

    @Override
    public String getMessage(){
        return new String(infolog);
    }
}
