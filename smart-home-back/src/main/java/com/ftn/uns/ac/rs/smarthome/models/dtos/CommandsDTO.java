package com.ftn.uns.ac.rs.smarthome.models.dtos;

import com.ftn.uns.ac.rs.smarthome.models.UserIdUsernamePair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandsDTO {
    private List<CommandSummary> commands;
    private List<UserIdUsernamePair> allUsers;
}
