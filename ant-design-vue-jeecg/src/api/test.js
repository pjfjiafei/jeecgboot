import { getAction, deleteAction, putAction, postAction, httpAction } from '@/api/manage'
import { axios } from '@/utils/request'

/*export function hello() {
  return axios({
    url: '/test/hello',
    method: 'get'
  })
}*/
const hello = ()=>getAction("/test/hello");
export {
  hello
}