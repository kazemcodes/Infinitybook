/*
 * Copyright (C) 2018 The Tachiyomi Open Source Project
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.ireader.core_api.http

import io.ktor.client.HttpClient

interface HttpClientsInterface {
  val browser: BrowserEngine
  val default: HttpClient
  val cloudflareClient: HttpClient
}
expect class HttpClients : HttpClientsInterface{
  override val browser: BrowserEngine
  override val default: HttpClient
  override val cloudflareClient: HttpClient
}