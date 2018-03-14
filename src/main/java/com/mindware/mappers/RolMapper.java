package com.mindware.mappers;

import com.mindware.model.Rol;

import java.util.List;

public interface RolMapper {
    void insertaRol(Rol rol);
    void updateRol(Rol rol);
    List<Rol> findAllRol();
    Rol findAllRolOpcionsMenuByRolId(int rolId);
}
