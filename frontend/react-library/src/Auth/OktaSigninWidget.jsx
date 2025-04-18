import {useEffect,useRef} from 'react';
import '@okta/okta-signin-widget/dist/css/okta-sign-in.min.css';
import { oktaConfig } from '../lib/oktaConfig';
 import OktaSignIn from '@okta/okta-signin-widget';
 
const OktaSignInWidget = ({onSuccess, onError}) => {
    const widgetRef = useRef();
 
    useEffect(() => {
        if(!widgetRef.current) {
            return false;
        }
        const widget = new OktaSignIn(oktaConfig);
        widget.showSignInToGetTokens({
            el: widgetRef.current,
        }).then(onSuccess).catch(onError);
        return () => widget.remove();
    }, [onSuccess,onError]);
 
 
       return(
           <div className="container mt-5 mb-5">
           <div ref={widgetRef}> </div>
           </div> 
        );
    };
 
export default OktaSignInWidget;