package kr.weit.roadyfoody.support.utils

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

fun MockHttpServletRequestBuilder.withAuth(): MockHttpServletRequestBuilder = this.header("userid", 1)

fun getWithAuth(url: String) = get(url).withAuth()

fun headWithAuth(url: String) = head(url).withAuth()

fun postWithAuth(url: String) = post(url).withAuth()

fun multipartWithAuth(url: String) = multipart(url).withAuth()

fun patchWithAuth(url: String) = patch(url).withAuth()

fun putWithAuth(url: String) = put(url).withAuth()

fun deleteWithAuth(url: String) = delete(url).withAuth()
