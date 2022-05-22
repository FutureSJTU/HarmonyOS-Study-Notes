// @ts-nocheck
/*
 * Copyright (c) 2021 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import commonOperator from '../../../share/common/util/commonOperator.js';
import Log from '../../common/util/log.js';

export default {
  props: ['productId', 'showObj', 'title','macAddress'],
  computed: {
    showMenu() {
      Log.info('length = ' + (this.menuOptions && this.menuOptions.length > 0));
      return !!(this.menuOptions && this.menuOptions.length > 0);
    },
    displayedTitle() {
      if (this.title === undefined) {
        Log.info('name = ' + this.$t('resources.' + this.productId));
        Log.info('name = ' + this.$t('resources.' + this.productId).device_name);
        return this.$t('resources.' + this.productId).device_name;
      } else {
        return this.title;
      }
    }
  },
  returnBack() {
    Log.info('returnBack');
    this.$emit('backClicked');
  },
  share() {
    commonOperator.hwShare(this.productId,this.macAddress);
  }
};
