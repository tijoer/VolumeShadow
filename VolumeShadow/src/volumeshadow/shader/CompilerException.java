package volumeshadow.shader;

public class CompilerException extends Exception {
    byte[] infolog;
    
    public CompilerException(byte[] infolog){
        this.infolog = infolog;
    }

    @Override
    public String getMessage(){
        return new String(infolog);
    }
}
