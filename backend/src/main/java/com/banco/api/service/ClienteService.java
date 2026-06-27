package com.banco.api.service;

import com.banco.api.dto.ClienteDTO;
import com.banco.api.entity.Cliente;
import com.banco.api.exception.CustomException;
import com.banco.api.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ClienteDTO> getAllClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClienteDTO getClienteById(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new CustomException("Cliente no encontrado con ID: " + id));
        return convertToDTO(cliente);
    }

    @Transactional
    public ClienteDTO createCliente(ClienteDTO dto) {
        if (clienteRepository.findByClienteid(dto.getClienteid()).isPresent()) {
            throw new CustomException("El ID de cliente ya está registrado");
        }
        Cliente cliente = convertToEntity(dto);
        Cliente saved = clienteRepository.save(cliente);
        return convertToDTO(saved);
    }

    @Transactional
    public ClienteDTO updateCliente(Integer id, ClienteDTO dto) {
        Cliente existing = clienteRepository.findById(id)
                .orElseThrow(() -> new CustomException("Cliente no encontrado con ID: " + id));
        
        existing.setNombre(dto.getNombre());
        existing.setGenero(dto.getGenero());
        existing.setEdad(dto.getEdad());
        existing.setIdentificacion(dto.getIdentificacion());
        existing.setDireccion(dto.getDireccion());
        existing.setTelefono(dto.getTelefono());
        existing.setContrasena(dto.getContrasena());
        existing.setEstado(dto.getEstado());
        
        // Only update if it doesn't clash with another cliente
        if (!existing.getClienteid().equals(dto.getClienteid())) {
            if (clienteRepository.findByClienteid(dto.getClienteid()).isPresent()) {
                throw new CustomException("El ID de cliente ya está registrado por otro usuario");
            }
            existing.setClienteid(dto.getClienteid());
        }

        Cliente updated = clienteRepository.save(existing);
        return convertToDTO(updated);
    }

    @Transactional
    public void deleteCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new CustomException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteDTO convertToDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getGenero(),
                cliente.getEdad(),
                cliente.getIdentificacion(),
                cliente.getDireccion(),
                cliente.getTelefono(),
                cliente.getClienteid(),
                cliente.getContrasena(),
                cliente.getEstado()
        );
    }

    private Cliente convertToEntity(ClienteDTO dto) {
        Cliente c = new Cliente();
        c.setId(dto.getId());
        c.setNombre(dto.getNombre());
        c.setGenero(dto.getGenero());
        c.setEdad(dto.getEdad());
        c.setIdentificacion(dto.getIdentificacion());
        c.setDireccion(dto.getDireccion());
        c.setTelefono(dto.getTelefono());
        c.setClienteid(dto.getClienteid());
        c.setContrasena(dto.getContrasena());
        c.setEstado(dto.getEstado());
        return c;
    }
}
