package org.opennms.horizon.alertservice.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
public class EventMatch extends TenantAwareEntity implements Serializable  {

    @Serial
    private static final long serialVersionUID = 5352121937366809116L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_match_id")
    private long id;


    @ManyToOne
    @JoinColumn(name="alert_definition_id")
    private AlertDefinition alertDefinition;

    private String name;
    private String value;
}
