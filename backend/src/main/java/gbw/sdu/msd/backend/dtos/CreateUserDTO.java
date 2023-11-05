package gbw.sdu.msd.backend.dtos;

public record CreateUserDTO(String username, String passwordHash, String name, String phoneNumber, String email) {
}
