package com.jodelXposed;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;

import com.jodelXposed.hooks.LayoutHooks;
import com.jodelXposed.utils.Hooks;
import com.jodelXposed.utils.Options;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static com.jodelXposed.utils.Log.dlog;
import static com.jodelXposed.utils.Log.xlog;
import static com.jodelXposed.utils.Utils.getSystemContext;

public class App implements IXposedHookLoadPackage,IXposedHookZygoteInit, IXposedHookInitPackageResources {

    public static String MODULE_PATH = null;


    @SuppressLint("DefaultLocale")
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tellm.android.app"))
            return;

        if (lpparam.packageName.equals("com.tellm.android.app")) {

            try {
                PackageInfo pkgInfo = getSystemContext().getPackageManager().getPackageInfo(lpparam.packageName, 0);
                dlog(String.format("----------%n" +
                        "             Starting JodelXposed%n" +
                        "             Version %s (%d)%n" +
                        "             JodelTarget %s (%d)%n" +
                        "             JodelLocal %s (%d)%n" +
                        "             ----------%n",
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    BuildConfig.JODEL_VERSION_NAME,
                    BuildConfig.JODEL_VERSION_CODE,
                    pkgInfo.versionName,
                    pkgInfo.versionCode
                ));
            }catch(Exception e){
                e.printStackTrace();
                xlog("Information cannot be gathered");
            }
            try {
                Options.getInstance();
            }catch (Exception e){
                e.printStackTrace();
                xlog("Options cannot be loaded");
            }

            Hooks hooks = new Hooks(lpparam);

            dlog("**** Locating method and field names ****");
            hooks.findHooks();

            dlog("#### Loading hooks ####");
            hooks.hook();
        }
    }

    @Override
    public void handleInitPackageResources(final XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.tellm.android.app"))
            return;

        xlog("Adding resources");

        new LayoutHooks(resparam);
    }


    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

}
