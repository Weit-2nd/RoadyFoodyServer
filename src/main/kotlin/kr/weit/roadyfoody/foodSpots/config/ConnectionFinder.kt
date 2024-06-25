package kr.weit.roadyfoody.foodSpots.config

import com.zaxxer.hikari.pool.HikariProxyConnection
import org.geolatte.geom.codec.db.oracle.DefaultConnectionFinder
import java.lang.reflect.Field
import java.sql.Connection

class ConnectionFinder : DefaultConnectionFinder() {
    // hibernate spatial이 커넥션을 못 찾는 문제 해결하기 위한 코드.
    // https://stackoverflow.com/questions/47753350/couldnt-get-at-the-oraclespatial-connection-object-from-the-preparedstatement
    override fun find(con: Connection): Connection {
        val delegate: Field = (con as HikariProxyConnection).javaClass.superclass.getDeclaredField("delegate")
        delegate.setAccessible(true)
        return delegate.get(con) as Connection
    }
}
