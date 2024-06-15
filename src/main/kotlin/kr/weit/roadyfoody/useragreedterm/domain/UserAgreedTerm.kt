package kr.weit.roadyfoody.useragreedterm.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.weit.roadyfoody.common.domain.BaseModifiableEntity
import kr.weit.roadyfoody.term.domain.Term
import kr.weit.roadyfoody.user.domain.User

@Entity
@Table(
    name = "user_agreed_terms",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "term_id"]),
    ],
    indexes = [
        Index(name = "user_agreed_terms_agreed_flag_idx", columnList = "agreed_flag"),
        Index(name = "user_agreed_terms_user_id_idx", columnList = "user_id"),
        Index(name = "user_agreed_terms_term_id_idx", columnList = "term_id"),
    ],
)
@SequenceGenerator(name = "USER_AGREED_TERMS_SEQ_GENERATOR", sequenceName = "user_agreed_terms_seq", initialValue = 1, allocationSize = 1)
class UserAgreedTerm(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_AGREED_TERMS_SEQ_GENERATOR")
    val id: Long = 0L,
    @Column(name = "agreed_flag")
    var agreedFlag: Boolean = true,
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    @ManyToOne
    @JoinColumn(name = "term_id", nullable = false)
    val term: Term,
) : BaseModifiableEntity()
