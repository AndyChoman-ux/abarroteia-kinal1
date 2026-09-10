package main.java.com.vyorg.abarroteria.kinal.service;

import main.java.com.vyorg.abarroteria.kinal.dto.request.LoginDTORequest;
import main.java.com.vyorg.abarroteria.kinal.dto.response.LoginDTOResponse;
import main.java.com.vyorg.abarroteria.kinal.model.Usuario;
import main.java.com.vyorg.abarroteria.kinal.repository.AuthRepository;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    
    private final AuthRepository authRepository;
  
    public AuthService(AuthRepository authRepository){
        this.authRepository = authRepository;
    }

    public AuthService() {
        throw new UnsupportedOperationException("Not supported yet."); 
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

        if(response == null){
    throw new RuntimeException("No existe un usuario con ese correo");
}

        if(response.getContrasenaHash() == null){
            throw new RuntimeException("El usuario no tiene contraseña registrada");
        }

        if(BCrypt.checkpw(loginDTORequest.getPassword(), response.getContrasenaHash())){
            return response;
        }

        throw new RuntimeException("Contraseña incorrecta");
    }
    
    public boolean registrar(String nombre, String apellido, String email, String password){
    if (nombre == null || nombre.isBlank() ||
        apellido == null || apellido.isBlank() ||
        email == null || email.isBlank() ||
        password == null || password.isBlank()){
        throw new IllegalArgumentException("Todos los campos son obligatorios");
    }

    String hash = BCrypt.hashpw(password, BCrypt.gensalt());

    Usuario nuevoUsuario = new Usuario();
    nuevoUsuario.setNombre(nombre);
    nuevoUsuario.setApellido(apellido);
    nuevoUsuario.setEmail(email);
    nuevoUsuario.setIdRol(2);
    nuevoUsuario.setContrasena_hash(hash);   

    return authRepository.registrarUsuario(nuevoUsuario);
}
    
}
