package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.dto.request.LoginDTORequest;
import main.java.com.vyorg.abarroteria.kinal.dto.response.LoginDTOResponse;
import main.java.com.vyorg.abarroteria.kinal.repository.AuthRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    

    private final AuthRepository authRepository;
    
    
    public AuthService(AuthRepository authRepository){
        this.authRepository = authRepository;
    }
    public LoginDTOResponse login(LoginDTORequest loginDTORequest){
       
        if(loginDTORequest == null){
           
            throw new RuntimeException("Los datos estan vacios");
        }else if(loginDTORequest.getEmail()==null || loginDTORequest.getPassword()== null){
    
            throw new RuntimeException("Uno o los dos campos estan vacios");
        }else if(loginDTORequest.getEmail().isEmpty()||loginDTORequest.getPassword().isEmpty())   {
         
            throw new RuntimeException("No puedes dejar campos en blanco");   
        }
        
        LoginDTOResponse response = authRepository.findUserByEmail(loginDTORequest);
            
        if(response.getContrasenaHash() == null){
            throw new RuntimeException("");
        }else{
            
            if(BCrypt.checkpw(loginDTORequest.getPassword(), response.getContrasenaHash())){
                return response;
//new LoginDTOResponse(response.getNombre(), response.getApellido(),response.getIdRol()){      
     }            
        }
            return null;
}
    }
