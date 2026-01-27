package org.example.myphambe.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Tự động tạo Getter, Setter, toString, equals, hashCode
@NoArgsConstructor // Tạo constructor không tham số (Bắt buộc cho Jackson/JSON)
@AllArgsConstructor // Tạo constructor đầy đủ tham số
public class RegisterRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;



    // Bạn có thể thêm trường này nếu muốn bắt người dùng nhập lại mk ở backend
    // private String confirmPassword;
}