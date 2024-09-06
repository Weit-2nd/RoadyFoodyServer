package kr.weit.roadyfoody.global.utils

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

// findSlice의 경우 Slice<T?>를 반환하는데 순수하게 엔티티만 조회하는경우 null이 반환될 수 없으므로 Slice<T>로 강제 캐스팅해도 안전합니다.
// 대신 nullable 가능성이 있는 단순 컬럼 조회시에는 Slice<T?>로 반환되므로 그땐 getSlice가 아닌 findSlice를 사용해야 합니다.
// 참고 : https://github.com/line/kotlin-jdsl/issues/608
// Slice Page 조회시 Fetch조인을 사용하면 항상 에러가 발생합니다
// 참고 : https://github.com/line/kotlin-jdsl/issues/176#issuecomment-1404862616
fun <T : Any> KotlinJdslJpqlExecutor.getSlice(
    pageable: Pageable,
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): Slice<T> =
    findSlice(
        pageable,
        init,
    ) as Slice<T>

fun <T : Any> KotlinJdslJpqlExecutor.getPage(
    pageable: Pageable,
    init: Jpql.() -> JpqlQueryable<SelectQuery<T>>,
): Page<T> =
    findPage(
        pageable,
        init,
    ) as Page<T>

fun <T : Any> KotlinJdslJpqlExecutor.findList(init: Jpql.() -> JpqlQueryable<SelectQuery<T>>): List<T> = findAll(init) as List<T>

fun <T : Any> KotlinJdslJpqlExecutor.findMutableList(init: Jpql.() -> JpqlQueryable<SelectQuery<T>>): MutableList<T> =
    findAll(init) as MutableList<T>
