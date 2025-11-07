package hsf302.group5.agriculturalproductsmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id", updatable = false)
    private int categoryId;

    @Column(name = "category_name", length = 255, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Product> products;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
