package eus.ibai.urlshortener.entity;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "short_url")
//@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortUrl {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    @Column(length = 36)
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "short_key", nullable = false, unique = true)
    private String key;

    @Column(nullable = false)
    private String url;

    private boolean enabled;

    @CreationTimestamp
    @Column(name = "created_on")
    private Date createdOn;
}
