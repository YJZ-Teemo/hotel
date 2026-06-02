import { createApp } from 'vue'

import {
  Button, Icon, NavBar,
  Grid, GridItem,
  Swipe, SwipeItem,
  Form, Field, Cell, CellGroup,
  Calendar, Tag, Image as VanImage,
  Radio, RadioGroup,
  Stepper, Switch,
  Popup, TimePicker, Notify,Dialog
} from 'vant'

import './style.css'
import 'vant/lib/index.css'
import App from './App.vue'
import router from './router'
import api from './api'

const app = createApp(App)
app.config.globalProperties.$api = api

// Notify 用这种方式挂载
app.config.globalProperties.$notify = Notify

app.use(Button)
  .use(Icon)
  .use(NavBar)
  .use(Grid)
  .use(GridItem)
  .use(Swipe)
  .use(SwipeItem)
  .use(Form)
  .use(Field)
  .use(Cell)
  .use(CellGroup)
  .use(Calendar)
  .use(Tag)
  .use(VanImage)
  .use(Radio)
  .use(RadioGroup)
  .use(Stepper)
  .use(Switch)
  .use(Popup)
  .use(TimePicker)
  .use(Dialog)
  .use(router)

app.mount('#app')
