package elice.shoppingmallproject.domain.image.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductDto {
    private String productName;
    private String category;
    private String description;
    private Double price;
    private Integer stockS;
    private Integer stockM;
    private Integer stockL;
    private Integer stockXL;
    private String url;
}
