//go:build unit

/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package handlers

import (
	"github.com/stretchr/testify/assert"
	"testing"
)

func TestPostgresUpdateConfig(t *testing.T) {
	ConfigFilePath = "./../../charts/opennms/templates/"
	handler := PostgresHandler{}
	assert.Nil(t, handler.GetConfig(), "config should start as nil")
	err := handler.UpdateConfig(DefaultTestValues())
	assert.Nil(t, err)
	assert.NotNil(t, handler.GetConfig(), "config should no longer be nil")
}