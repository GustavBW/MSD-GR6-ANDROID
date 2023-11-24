package gbw.sdu.msd.backend.dtos;

import java.util.List;

public record GroupActivityDTO(UserDTO whoPaid, double amount, List<UserDTO> debtees) {
}
