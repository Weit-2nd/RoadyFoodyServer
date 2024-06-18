package kr.weit.roadyfoody.term.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity

@Entity
@Table(
    name = "terms",
    indexes = [
        Index(name = "terms_required_idx", columnList = "required"),
    ],
)
@SequenceGenerator(name = "TERMS_SEQ_GENERATOR", sequenceName = "terms_seq", initialValue = 1, allocationSize = 1)
class Term(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TERMS_SEQ_GENERATOR")
    val id: Long = 0L,
    @Column(nullable = false, length = 90, unique = true)
    var title: String,
    @Column(columnDefinition = "CLOB", nullable = false)
    var content: String,
    @Column(nullable = false)
    var required: Boolean,
) : BaseModifiableEntity()
