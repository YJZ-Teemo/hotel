import { createApp } from 'vue'

import { 
    Button,Icon, NavBar,
    Grid, GridItem,
    Swipe, SwipeItem,
    Form, Field, Cell, CellGroup,
    Calendar,Tag,Image as VanImage,
    Radio, RadioGroup,
    Stepper, Switch,
    Popup, TimePicker,Notify
} from 'vant';
import './style.css'
import 'vant/lib/index.css'
import App from './App.vue'
import router from './router'
import api from './api'

const app = createApp(App)
app.config.globalProperties.$api = api

app.use(Button).use(router).use(Icon)
.use(Grid).use(GridItem)
.use(Swipe).use(SwipeItem)
.use(Form).use(Field).use(Cell).use(CellGroup)
.use(Calendar).use(Tag).use(VanImage)
.use(NavBar).use(Radio).use(RadioGroup).use(Stepper).use(Switch)
.use(Popup).use(TimePicker).use(Notify)
app.mount('#app')
